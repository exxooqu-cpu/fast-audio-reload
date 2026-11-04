from PIL import Image, ImageDraw
import math, os

S = 2048           # supersampled canvas
OUT = 128          # final size
BG = (30, 23, 38)
PINK = (255, 95, 176)
LIGHT = (255, 242, 249)

img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
d = ImageDraw.Draw(img)

# --- background: rounded square -------------------------------------------
d.rounded_rectangle([0, 0, S - 1, S - 1], radius=int(S * 0.22), fill=BG)

cx = cy = S // 2

# --- reload ring ----------------------------------------------------------
R = int(S * 0.325)          # ring centre radius
W = int(S * 0.072)          # stroke width
gap_a, gap_b = 296.0, 356.0 # gap in the upper-right quadrant

d.arc(
    [cx - R, cy - R, cx + R, cy + R],
    start=gap_b,
    end=gap_a,
    fill=PINK,
    width=W,
)

# --- arrowhead at the end of the sweep ------------------------------------
a = math.radians(gap_a)
px, py = cx + R * math.cos(a), cy + R * math.sin(a)
tx, ty = -math.sin(a), math.cos(a)          # clockwise tangent
nx, ny = math.cos(a), math.sin(a)           # radial normal

tip = (px + tx * W * 0.95, py + ty * W * 0.95)
base = (px - tx * W * 0.45, py - ty * W * 0.45)
half = W * 1.00
d.polygon(
    [
        tip,
        (base[0] + nx * half, base[1] + ny * half),
        (base[0] - nx * half, base[1] - ny * half),
    ],
    fill=PINK,
)

# --- speaker --------------------------------------------------------------
hw, hh = S * 0.150, S * 0.145     # cone half-height / half-width
bw, bh = S * 0.048, S * 0.050     # body half-width / half-height

# shift the whole speaker so its bounding box is optically centred
ox = (hw + bw * 2.1) - hw * 0.92
ox *= 0.5

body = [cx - hw - bw * 2.1 + ox, cy - bh, cx - hw * 0.50 + ox, cy + bh]
d.rounded_rectangle(body, radius=int(bh * 0.45), fill=LIGHT)

d.polygon(
    [
        (cx - hw * 0.55 + ox, cy - bh),
        (cx + hw * 0.92 + ox, cy - hh),
        (cx + hw * 0.92 + ox, cy + hh),
        (cx - hw * 0.55 + ox, cy + bh),
    ],
    fill=LIGHT,
)

img = img.resize((OUT, OUT), Image.LANCZOS)
dst = r"C:\Users\brawl\IdeaProjects\FastAudioReload\src\main\resources\assets\fastaudioreload\icon.png"
img.save(dst, "PNG", optimize=True)
print("saved", dst, os.path.getsize(dst), "bytes", img.size)
