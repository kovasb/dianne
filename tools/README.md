#Neural Network models

Put your neural network models in a subfolder named `models`

Each network has its own folder with in it:
- modules.txt : JSON description of the modules
- layout.txt : the layout of the modules to show them in the Dianne UI
- XXX : binary files containing the weights of the module with XXX the module UUID

You can download some examples at http://dianne.intec.ugent.be/dianne/models/


#Datasets

Put your datasets in a subfolder named `datasets`

You can download some datasets at http://dianne.intec.ugent.be/dianne/datasets/

Supported datasets:

MNIST
-----
Hand written digit images of 28x28 pixels greyscale. 

Provide the following files in the MNIST/ folder
 t10k-images.idx3-ubyte
 t10k-labels.idx1-ubyte
 train-images.idx3-ubyte
 train-labels.idx1-ubyte
 
SVHN
----
Google StreetView HouseNumbers dataset

Provide the following files in the SVHN/ folder
 train_images.bin
 train_labels.bin
 test_images.bin
 test_labels.bin

CIFAR-10
--------
Image patches of 10 different object classes, 32x32 rgb

Provide the following files in the CIFAR-10/ folder
 batches.meta.txt
 data_batch_1.bin
 data_batch_2.bin
 data_batch_3.bin
 data_batch_4.bin
 data_batch_5.bin
 test_batch.bin

CIFAR-100
---------
Image patches of 100 fine grained and 10 coarse grained object classes, 32x32 rgb

Provide the following files in the CIFAR-100/ folder
 coarse_label_names.txt
 fine_label_names.txt
 test.bin
 train.bin

STL-10
------
Image patches of 10 object classes, 96x96 rgb

Provide the following files in the STL-10/ folder
 class_names.txt
 fold_indices.txt
 test_X.bin
 test_y.bin
 train_X.bin
 train_y.bin
 unlabeled_X.bin

ImageNet
--------
Images from the ImageNet dataset. At this moment we support the validation set of the ILSVRC2012 challenge.

Provide the following files in the ImageNet/ folder
 classes.txt  - containing all class names
 outputs.txt  - containing all outputs of the images
 images/ 

The images/ folder contains a .JPEG file for each input image 
with the naming scheme: ILSVRC2012_val_000XXXXX.JPEG
