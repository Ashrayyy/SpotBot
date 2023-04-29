# SpotBot
SpotBot is an Android app that leverages the power of machine learning and android development to help find missing people and wanted criminals. It uses the FaceNet ML model (tflite) for facial recognition to match faces to a database of missing persons or criminals, and can make it an invaluable tool for law enforcement and search and rescue organizations.

## Challenges
Throughout the development process, I faced a number of challenges, such as optimizing the model to work well on mobile devices and designing an intuitive user interface. But with perseverance and dedication, I'm proud to say that the app has exceeded my expectations.

This is my first time using ML models in and android project, I have used the already trained FaceNet models from [here](https://github.com/shubham0204/FaceRecognition_With_FaceNet_Android/tree/master/app/src/main/assets)

### Face Detection
Face detection is a common computer vision task that involves identifying and localizing faces in an image or video stream. It has many applications, such as photography, video editing, and security systems. 

#### Google MLKit
For Face Detection, I've employed the use of Google ML Kit, a pre-trained machine learning model that detects faces in images and video streams. It uses a deep learning architecture that has been trained on a large dataset of faces to identify facial features and localize faces with high accuracy.

The ML Kit Face Detection model can detect multiple faces in an image or video stream, and provides information about the position, size, and orientation of each detected face. It can also identify landmarks on each face, such as the eyes, nose, and mouth, and estimate the pose of each face. It also provides various options that you can use to configure your use type, i.e., to detect facial landmarks/smiling probabilities, etc.
You can find more about it [here] (https://developers.google.com/ml-kit/vision/face-detection/android).

### Face Recognition
Face recognition is the process of identifying or verifying the identity of a person based on their face. It has many applications, such as security systems, social media, and entertainment. One popular approach to face recognition is using machine learning models, such as the FaceNet model, to extract features from facial images and compare them to a database of known faces.

#### FaceNet Model
FaceNet is a deep learning model for face recognition developed by researchers at Google. It is trained on a large dataset of faces and learns to map each face to a high-dimensional feature space, where similar faces are closer together and dissimilar faces are farther apart. This makes it possible to compare faces based on their feature vectors and identify matches.

The FaceNet model uses a deep convolutional neural network (CNN) architecture to extract features from facial images. The CNN consists of multiple layers that learn increasingly complex representations of the input image, culminating in a high-dimensional feature vector. The model is trained to minimize the distance between feature vectors of matching faces and maximize the distance between feature vectors of non-matching faces.

## Working
1. Faces are detected in an image and are stored in a list.
2. Bounding boxes are created out of the faces, and the image is copied and copped to get only the face.
3. Since the required format for input image for FaceNet model is Bitmap, we process the images using the BitmapFactory to convert them to bitmap.
4. FaceNet model's instance is created and is used to generate an array of 128 Floating point integers by passing the cropped face's Bitmap to it.
5. Embeddings are extracted for the image of person that is missing and then these embeddings are compared with the Embeddings of every person present in the refrence images.
6. We can calculate the distance between two of such arrays using L2 Norm or Cosine Similarity (I've used L2 Norm in the project, it simply calculates the Eucledian distance between the arrays).
7. The closest match in the reference image (one with the least distance with the person's face) is returned and is displayed below.
8. For Learning purpose, I've also displayed the 128 Floating point array (Face Embeddings) in a text view after the output.


Acknowledgments
SpotBot would not have been possible without the following resources and tools:

TensorFlow Lite for mobile devices
The FaceNet ML model
Android Studio
Google's ML Kit
Contact
If you have any questions or feedback about SpotBot, feel free to contact us at [email address].
