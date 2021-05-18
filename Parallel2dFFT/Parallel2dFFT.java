package Parallel2dFFT;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Parallel2dFFT extends Thread {

    // Image Size
    public static int N = 2048;

    // Number of Threads
    final static int P = 4;
    // Variable me for Thread Parameter
    int me;
    // Creation of CyclicBarrier used to synchronise data between threads
    static CyclicBarrier cyclicBarrier =  new CyclicBarrier(P);
    // Creates variable B, used to determine the amount of 'work' each thread is allocated
    final static int B = N/P;
    // Creation of the empty two dimensional arrays used to copy data to and from during the fourier transformations
    static double[][] CRe = new double[N][N], CIm = new double[N][N];
    static double[][] reconRe = new double[N][N], reconIm = new double[N][N];

    // Class constructor
    public Parallel2dFFT(int me) {
        this.me = me;

    }

    public static void main(String[] args) throws Exception {

        // Creation of Two Dimensional Array: Column Size - N Row Size - N
        double[][] X = new double[N][N];
        // Reading of the greyscale image
//      ReadPGM.read(X, "lena.ascii.pgm", N);
        readPNG(X, "C://Users/Bradley/Desktop/realterry.png");


        DisplayDensity display =
                new DisplayDensity(X, N, "Original Image"); // Displays the Original image

        // Create array for in-place FFT, and copy original data to it
        for (int k = 0; k < N; k++) {
            for (int l = 0; l < N; l++) {
                CRe[k][l] = X[k][l];
            }
        }

        //TODO: Creation of threads based on number of P
        Parallel2dFFT[] threads = new Parallel2dFFT[P];
        for (int i = 0; i < P; i++) {
            threads[i] = new Parallel2dFFT(i);
            threads[i].start();
        }

        long startTime = System.currentTimeMillis(); // Take recording of time at start of runtime

        for (int i = 0; i < P; i++) {
            threads[i].join();
        }

        // Record End Time
        long endTime = System.currentTimeMillis();
        // Output End Time - Start Time
        System.out.println(endTime - startTime + "ms");
    }

    //TODO: Cyclic barrier synchronisation method, used for java threads as seen in Lab...
    static void synch() {
        try {
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Transpose Array containing Image information.
    static void transpose(double[][] a) {

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                 double current = a[i][j];
                 a[i][j] = a[j][i];
                 a[j][i] = current;
            }
        }
    }

    public void run() {

        // Call to fft2d to perform first Fourier Transform
        fft2d(CRe, CIm, 1);  // Fourier transform

        // Displays Fourier Transformed Image
        if (me == 0) {
            Display2dFT display2 =
                    new Display2dFT(CRe, CIm, N, "Discrete FT");
        }

        // Create array for in-place inverse FFT, and copy FT to it
        for (int k = 0; k < N; k++) {
            for (int l = 0; l < N; l++) {
                reconRe[k][l] = CRe[k][l];
                reconIm[k][l] = CIm[k][l];
            }
        }

        // Sync thread data
        synch();

        // Inverse Fourier Transform to reconstruct Image
       fft2d(reconRe, reconIm, -1);  // Inverse Fourier transform


        // Outputs reconstructed image from Inverse Fourier Transform
        if (me == 0) {
            DisplayDensity display3 =
                    new DisplayDensity(reconRe, N, "Reconstructed Image");
        }
   }

    /* Possibly: Loop here using begin and end instead of N (if that's applicable), then let
        the transpose happen before looping the same way again?
     */
    public void fft2d(double [] [] re, double [] [] im, int isgn) {

        // Values begin and end used to split 'work' between threads
        int begin = me * B;
        int end = begin + B;

//        System.out.println(begin);
//        System.out.println(end);

        // Run 1dFFT on the threads total amount of work
        for (int i = begin; i < end; i++){
            FFT.fft1d(re[i], im[i], isgn);
        }

        // Sync thread data
        synch();

        // Transpose on thread 0
        if (me == 0) {
            transpose(re) ;
            transpose(im) ;
        }

        // Sync thread data
        synch();

        // Run 1dFFT on the threads total amount of work
        for (int i = begin; i < end; i++){
            FFT.fft1d(re[i], im[i], isgn);
        }

        // Sync thread data
        synch();

        // Transpose on thread 0
        if (me == 0) {
            transpose(re) ;
            transpose(im) ;
        }
}

    // Function used to readPNG files as if they were a PGM file using image density
    public static void readPNG(double[][] density, String filename) {

        BufferedImage img = null; // or whatever
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Raster ras = img.getData();
        for (int i = 0 ; i < N ; i++) {
            for (int j = 0 ; j < N ; j++) {
                density [i] [N - 1 - j] = ras.getSample(i, j, 0) ; }
        }
    }
}

