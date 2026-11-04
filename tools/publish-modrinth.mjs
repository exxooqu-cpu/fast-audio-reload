#!/usr/bin/env node
// Uploads Fast Audio Reload to Modrinth.
//
//   MODRINTH_TOKEN=mrp_... node tools/publish-modrinth.mjs
//   node tools/publish-modrinth.mjs --project <id> --skip-project
//
// The token can also live in .modrinth-token next to the repo root.

import { readFileSync, readdirSync, existsSync } from "node:fs";
import { join, basename } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(fileURLToPath(import.meta.url), "..", "..");
const api = "https://api.modrinth.com/v2";
const agent = "exxooqu/fast-audio-reload";

const argv = process.argv.slice(2);
const has = (flag) => argv.includes(flag);
const value = (flag) => {
  const i = argv.indexOf(flag);
  return i >= 0 ? argv[i + 1] : undefined;
};

const token =
  process.env.MODRINTH_TOKEN ||
  (existsSync(join(root, ".modrinth-token"))
    ? readFileSync(join(root, ".modrinth-token"), "utf8").trim()
    : "");

if (!token) {
  console.error("No Modrinth token. Set MODRINTH_TOKEN or create .modrinth-token.");
  process.exit(1);
}

async function call(method, path, { json, form } = {}) {
  const headers = { Authorization: `Bearer ${token}`, "User-Agent": agent };
  let body;

  if (json) {
    headers["Content-Type"] = "application/json";
    body = JSON.stringify(json);
  } else if (form) {
    body = form;
  }

  const res = await fetch(api + path, { method, headers, body });
  const text = await res.text();
  if (!res.ok) {
    throw new Error(`${method} ${path} -> ${res.status}\n${text}`);
  }
  return text ? JSON.parse(text) : null;
}

const meta = JSON.parse(readFileSync(join(root, "tools/modrinth-project.json"), "utf8"));
const body = readFileSync(join(root, meta.body_file), "utf8");

const projectPayload = {
  slug: meta.slug,
  title: meta.title,
  description: meta.summary,
  body,
  project_type: "mod",
  categories: meta.categories,
  client_side: meta.client_side,
  server_side: meta.server_side,
  environment: meta.environment,
  license_id: meta.license_id,
  source_url: meta.source_url,
  issues_url: meta.issues_url,
  is_draft: has("--draft"),
  initial_versions: [],
};

const iconPath = join(root, meta.icon);
const hasIcon = existsSync(iconPath);

let projectId = value("--project") || process.env.MODRINTH_PROJECT_ID;
let iconUploaded = false;

if (!has("--skip-project")) {
  if (projectId) {
    await call("PATCH", `/project/${projectId}`, { json: projectPayload });
    console.log(`updated project ${projectId}`);
  } else {
    // Creating a project is multipart only and always starts as a draft.
    const form = new FormData();
    form.append("data", JSON.stringify({ ...projectPayload, is_draft: true }));
    if (hasIcon) {
      form.append("icon", new Blob([readFileSync(iconPath)], { type: "image/png" }), "icon.png");
      iconUploaded = true;
    }
    const project = await call("POST", "/project", { form });
    projectId = project.id;
    console.log(`created project ${project.slug} -> ${projectId}`);
  }
}

if (!projectId) {
  console.error("Project id unknown. Pass --project <id>.");
  process.exit(1);
}

if (!has("--skip-icon") && !iconUploaded) {
  if (hasIcon) {
    const form = new FormData();
    form.append("icon", new Blob([readFileSync(iconPath)], { type: "image/png" }), "icon.png");
    await call("PATCH", `/project/${projectId}/icon?ext=png`, { form });
    console.log("icon uploaded");
  } else {
    console.warn(`icon not found: ${iconPath} (run tools/make-icon.py)`);
  }
}

if (has("--skip-versions")) {
  console.log(`done, project ${projectId}`);
  process.exit(0);
}

const existing = new Set(
  (await call("GET", `/project/${projectId}/version`)).map((v) => v.version_number),
);

const versionsDir = join(root, "versions");
const builds = [];

for (const dir of readdirSync(versionsDir).sort()) {
  const libs = join(versionsDir, dir, "build", "libs");
  if (!existsSync(libs)) continue;

  const minecraft = dir.replace(/-fabric$/, "");
  for (const file of readdirSync(libs)) {
    if (!file.endsWith(".jar") || file.includes("-sources")) continue;
    builds.push({ minecraft, jar: join(libs, file) });
  }
}

if (builds.length === 0) {
  console.error("No jars found. Run `gradlew build` first.");
  process.exit(1);
}

let uploaded = 0;
let skipped = 0;

for (const { minecraft, jar } of builds) {
  const versionNumber = `1.0.0+${minecraft}`;

  if (existing.has(versionNumber)) {
    console.log(`skip ${versionNumber}`);
    skipped += 1;
    continue;
  }

  const data = {
    name: `1.0.0 for Minecraft ${minecraft}`,
    version_number: versionNumber,
    changelog: `First release. Build for Minecraft ${minecraft}.`,
    dependencies: [],
    game_versions: [minecraft],
    version_type: "release",
    loaders: ["fabric"],
    environment: "client_only",
    client_side: "required",
    server_side: "unsupported",
    featured: false,
    project_id: projectId,
    file_parts: ["file"],
  };

  const form = new FormData();
  form.append("data", JSON.stringify(data));
  form.append(
    "file",
    new Blob([readFileSync(jar)], { type: "application/java-archive" }),
    basename(jar),
  );

  const created = await call("POST", "/version", { form });
  console.log(`uploaded ${versionNumber} -> ${created.id}`);
  uploaded += 1;
  await new Promise((r) => setTimeout(r, 250));
}

console.log(`\nhttps://modrinth.com/mod/${meta.slug}`);
console.log(`uploaded ${uploaded}, skipped ${skipped}`);

if (has("--submit")) {
  await call("PATCH", `/project/${projectId}`, { json: { requested_status: "approved" } });
  console.log("submitted for review");
}
