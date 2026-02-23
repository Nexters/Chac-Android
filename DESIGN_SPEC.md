# Project Specification: Android On-Device Semantic Photo Search

## 1. Project Overview
This project aims to build an Android application that allows users to search for photos stored on their device using natural language prompts (e.g., "Find photos of dogs," "Sunset at the beach").
The core technology involves using a quantized **CLIP (Contrastive Language-Image Pre-training)** model via **ONNX Runtime** to generate embeddings for both images and text, and then performing vector similarity search.

## 2. Tech Stack & Dependencies
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Asynchronous Processing:** Kotlin Coroutines & Flow
* **Machine Learning:** * `com.microsoft.onnxruntime:onnxruntime-android` (Latest stable version)
    * Models: `image_encoder.quant.onnx`, `text_encoder.quant.onnx` (placed in `assets/`)
* **Local Database:** Room Database (for caching image embeddings)
* **Image Loading:** Coil

## 3. Data Architecture

### 3.1 Room Database Schema
We need to cache the expensive image embedding calculations.

**Entity: `PhotoEmbedding`**
| Column Name | Type | Description |
| :--- | :--- | :--- |
| `id` | Long (PK) | MediaStore Image ID |
| `uri` | String | Content URI of the image |
| `embedding` | String | Float array converted to JSON String or Blob (The vector) |
| `timestamp` | Long | Added date (to handle stale data) |

### 3.2 Data Flow
1.  **Indexing Phase (Background Work):**
    * Scan `MediaStore` for images.
    * Check if the image ID exists in `PhotoEmbedding` DB.
    * If NOT exists:
        * Load Bitmap -> Preprocess (Resize/Normalize) -> **Run ONNX Image Encoder** -> Get Vector (Float Array).
        * Save Vector to DB.
2.  **Search Phase (User Interaction):**
    * User inputs text query.
    * Tokenize text -> **Run ONNX Text Encoder** -> Get Vector (Float Array).
    * Fetch all cached image vectors from DB.
    * Calculate **Cosine Similarity** between Text Vector and each Image Vector.
    * Sort images by similarity score (Descending).
    * Return Top K results to UI.

## 4. Key Components Implementation Details

### 4.1 `EmbeddingRepository`
* Responsible for interfacing with `MediaStore` and `RoomDB`.
* Function `syncImages()`: Iterates through gallery and processes un-indexed photos.

### 4.2 `OnnxModelManager` (Singleton)
* **Initialization:** Load ONNX environments and create sessions for both models.
* **Image Preprocessing:**
    * Input: `Bitmap`
    * Process: Resize to 224x224 (typical CLIP input), Normalize (Mean/Std depend on specific model), Convert to `FloatBuffer`.
* **Text Preprocessing (Tokenizer):**
    * Need a simple BPE Tokenizer or a mapping logic to convert String to Input IDs.
    * *Note for Codex:* Assume a `SimpleTokenizer` class exists that takes a vocabulary file and returns `IntArray`.

### 4.3 `VectorUtils`
* Function `calculateCosineSimilarity(v1: FloatArray, v2: FloatArray): Float`
    * Formula: `dot(v1, v2) / (norm(v1) * norm(v2))`
    * Note: Since CLIP vectors are often normalized, dot product might suffice, but implement full cosine similarity for safety.

## 5. UI Structure (Jetpack Compose)

* **`SearchScreen`**:
    * `TextField`: For user prompt input.
    * `LazyVerticalGrid`: Displays search results.
    * `LinearProgressIndicator`: Shows indexing progress (if running in background).
* **`SearchResultItem`**:
    * Displays image thumbnail using Coil.
    * (Optional) Overlay similarity score for debug.

## 6. Prompt Engineering Context for AI
When generating code, please adhere to:
* Use Hilt for Dependency Injection.
* Handle ONNX inference in `Dispatchers.Default` to avoid blocking the Main Thread.
* Error handling: If an image is corrupt, skip it and continue indexing.
* Keep the ONNX session open; do not close/reopen per image.
