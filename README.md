# SpotBot

SpotBot is an Android app that leverages the power of machine learning to help find missing people and wanted criminals. It uses the FaceNet ML model (tflite) for facial recognition to match faces to a database of missing persons or criminals, and can make it an invaluable tool for law enforcement and search and rescue organizations.

## Chalanges
Throughout the development process, I faced a number of challenges, such as optimizing the model to work well on mobile devices and designing an intuitive user interface. But with perseverance and dedication, I'm proud to say that the app has exceeded my expectations.

This is my first time using ML models in and android project (I have used the already trained FaceNet models from here (https://github.com/shubham0204/FaceRecognition_With_FaceNet_Android/tree/master/app/src/main/assets))

### Face Detection
Face detection is a common computer vision task that involves identifying and localizing faces in an image or video stream. It has many applications, such as photography, video editing, and security systems. For it, I've employed the use of Google ML Kit, a software development kit that provides pre-trained machine learning models for common tasks, including face detection. You can find more about it here (https://developers.google.com/ml-kit/vision/face-detection/android).

### Face Recognition
Face recognition is the process of identifying or verifying the identity of a person based on their face. It has many applications, such as security systems, social media, and entertainment. One popular approach to face recognition is using machine learning models, such as the FaceNet model, to extract features from facial images and compare them to a database of known faces.

#### FaceNet Model
FaceNet is a deep learning model for face recognition developed by researchers at Google. It is trained on a large dataset of faces and learns to map each face to a high-dimensional feature space, where similar faces are closer together and dissimilar faces are farther apart. This makes it possible to compare faces based on their feature vectors and identify matches.

The FaceNet model uses a deep convolutional neural network (CNN) architecture to extract features from facial images. The CNN consists of multiple layers that learn increasingly complex representations of the input image, culminating in a high-dimensional feature vector. The model is trained to minimize the distance between feature vectors of matching faces and maximize the distance between feature vectors of non-matching faces.
Contributing
If you're interested in contributing to SpotBot, feel free to fork the repository and submit a pull request. We welcome contributions from anyone, regardless of experience level or background.

License
SpotBot is licensed under the MIT License. See the LICENSE file for more details.

Acknowledgments
SpotBot would not have been possible without the following resources and tools:

TensorFlow Lite for mobile devices
The FaceNet ML model
Android Studio
Contact
If you have any questions or feedback about SpotBot, feel free to contact us at [email address].
