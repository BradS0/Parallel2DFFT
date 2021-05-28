# Parallel2DFFT
 A parallel version of a 2-dimensional fast Fourier Transform
 -  If you have cloned this repository from git, the root folder for the jar file is under:
    - "out/artifacts/SCS_Coursework_jar"
 
 ( Please refer to the pics/HowToExample.png when reading these instructions )
 
 The code has been compiled into a runnable jar file to be used in conjunction with bash to test the outcome.
 
 To run a single two-dimensional Fast Fourier Transform using the compiled jar, please initiate an instance of bash in the root project 
 folder and use the command below:
 
 	"java -jar SCS\ Coursework.jar A B pics/C"
 	where:
 	- A = Problem size, in this case a choice between [256,512,1024,2048,4096 or 8192]
 	- B = Thread Count
 	- C = Image name, all images used for the project are stored under pics/ where all PNG's are saved as shib___ where the ___ = 
 	their pixel size, e.g. shib512.png
 
 To test the automated scripting please use the script relevant to the image type you are testing. By default the project is set to test
 PNG's for which the console command is:
 
 	"sh TestScript.sh"
 
 Before doing this you will want to uncomment line 89 in Parallel2dFFT.java to stop the pausing of the testing script between each completed
 test.
 
 To test PGM images using their relevant script please uncomment line 55 and comment out line 56 in Parallel2dFFT.java to stop the code 
 from using the PNG method to read PGM images (This also applies when running individual tests with the java -jar command). The two PGM
 images used in this project can also be found under the pics/ folder with their relevant names.
 
 	wolf.pgm = 256x256 PGM Image
 	lena.ascii.pgm = 512x512 PGM Image
 
 To test the use of filtering, please uncomment lines 120-130 in Parallel2dFFT.java or lines 36-46 in FFTImageFiltering for sequential
 filtering (before running). 
 
 To run the sequential code, please find the FFTImageFiltering.java file stored under Lab1 and use in a compiler of your choice and following
 the previous instructions for the use of PNGs or PGMs with the correct file path and/or any filtering. Furthermore, you will need 
 to manually enter the N value (problem size) for this version of the code on line 11.
