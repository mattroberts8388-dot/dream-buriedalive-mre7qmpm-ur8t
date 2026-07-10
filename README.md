# Buried Alive

You wake up trapped in a collapsed underground cave. You have **two stone pickaxes**
and **one torch**. Your only goal: dig your way back to the surface before the
darkness gets to you.

When you first join a world (or after you respawn), the mod teleports you deep
underground, seals a small pocket of air around you with stone, wipes your
inventory, and gives you your starter kit. Break out, tunnel up, and reach the
open sky to win.

---

## How to build the mod (no Java install needed!)

GitHub can build the `.jar` file for you **for free**, right in your browser.
You do not need to install Java or any developer tools. Just follow these steps.

### Step 1 — Create a GitHub account
Go to https://github.com and sign up (it's free).

### Step 2 — Create a new repository
1. Click the **+** in the top-right corner → **New repository**.
2. Give it any name (for example `buried-alive`).
3. Leave everything else as default and click **Create repository**.

### Step 3 — Upload the mod files
1. On your new repository page, click the link that says
   **"uploading an existing file"**.
2. **Extract** the ZIP file you downloaded (double-click it) so you get a folder.
3. **⚠️ macOS users — VERY IMPORTANT:** the `.github` folder is **invisible** by
   default in Finder. If you don't upload it, the build will **never run**!
   - Open the extracted folder in Finder.
   - Press **Cmd + Shift + .** (Command, Shift, and the period key together).
   - Hidden files and folders (including `.github`) will now appear, slightly greyed out.
4. Go **INSIDE** the extracted folder. Select **ALL** files and folders inside it —
   including the hidden **`.github`** folder — and drag them into the GitHub
   upload box.
   - **Do NOT drag the outer folder itself.** Drag its **contents**.
5. Scroll down and click **Commit changes**.

### Step 4 — Let GitHub build it
1. Click the **Actions** tab at the top of your repository.
2. You'll see a build running. Wait about **2 minutes** for it to finish
   (green check mark).

### Step 5 — Download your mod
1. Click on the finished build run.
2. Scroll down to **Artifacts** and download **mod-jar**.
3. Unzip it — inside is your `.jar` file.

### Step 6 — Install into Minecraft
1. Make sure you have **Fabric Loader** installed for Minecraft **1.20.1**
   and the **Fabric API** mod in your mods folder.
2. Copy the `.jar` file into your `.minecraft/mods/` folder.
   - Windows: `%appdata%\.minecraft\mods`
   - macOS: `~/Library/Application Support/minecraft/mods`
3. Launch Minecraft with the Fabric profile and create/enter a world.

Have fun escaping!

---

## Requirements
- Minecraft **1.20.1**
- Fabric Loader **0.15.11+**
- Fabric API **0.92.2+1.20.1**