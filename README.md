# 🖊️ Whiteboard Android App

A feature-rich **offline Whiteboard application** built in **Kotlin** for Android-based **Interactive Flat Panels (IFPs)** and tablets. It allows users to draw freely, insert shapes and text, zoom, erase, and save content—all while preserving the canvas state in a structured JSON format.

---

## ✨ Features

### ✍️ Drawing
- Freehand drawing with customizable stroke **color** and **thickness**
- Smooth rendering using `Path` and `Canvas`

### 🔷 Shapes
- Draw **Rectangle** and **Circle** shapes using touch gestures
- Live preview of shape before final placement
- Customizable **color** and **stroke width**

### 🔠 Text Tool
- Add movable, editable **text boxes** anywhere on the canvas
- Choose font size and color

### 🧼 Eraser
- Erase specific strokes or shapes using touch detection
- Toggle eraser mode to remove selected canvas items

### 🔍 Pinch-to-Zoom and Pan
- Zoom in and out using **ScaleGestureDetector**
- Smooth pan support to navigate across large whiteboards

### 💾 Save & Load
- Save the entire canvas state as a structured **JSON**
- save entire canvas as **image**

### 🔒 Offline-First
- Completely **offline**, no internet permission required
- Designed for classroom use on Interactive Panels or tablets

---

## 📦 Tech Stack

| Tech | Details |
|------|---------|
| Language | Kotlin |
| Architecture | MVVM (Model-View-ViewModel) |
| UI | Custom Views + Canvas |
| State Handling | LiveData + StateFlow |
| Touch Input | GestureDetector, ScaleGestureDetector |
| Serialization | Gson / Kotlinx Serialization (JSON format) |

---

## 📁 Folder Structure

```
com.example.whiteboard/
│
├── view/ # Custom View (WhiteboardView)
├── viewModel/ # BoardViewModel (all state logic)
├── data/model/ # Data models: Stroke, Shape, TextBox, WhiteBoardData
├── utils/ # Utility classes (e.g., zooming, color tools)
├── MainActivity.kt # Main launcher activity
└── ...
```

---

## 🛠️ Setup Instructions

1. Clone this repository:
   ```bash
   git clone  https://github.com/Pradeep72767/WhiteBoard_IFP.git

2. Open in Android Studio Arctic Fox+

3. Sync Gradle and Run on:
    - Tablet
    - Interactive Flat Panel (IFP)
    - Android 8.0+ recommended

4. Optional:
    - Add your custom save location in internal/external storage.
    - Add functionality to export to PDF or image if needed.

---

## 🧑‍🏫 How to Use
    - Draw: Use your finger or stylus to draw
    - Zoom: Pinch with two fingers
    - Erase: Tap eraser icon and touch content to remove
    - Shape: Tap shape icon → drag to draw rectangle/circle
    - Text: Tap to add text box
    - Save: Save as JSON and Save as Image
    - Colors: Choose different colors
    - Delete: Delete all content of the whiteboard at once

---

## 📷 Screenshots

---

### 👨‍💻 Author
- Developed by Pradeep Prajapati
- 💼 Android Developer with 4+ years experience
- 📧 pradeep72767@gmail.com
- 🔗 LinkedIn(www.linkedin.com/in/pradeep-prajapati-225808170)
