package Parallel2dFFT;
import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.io.File;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Parallel2dFFT extends Thread {

    // Image Size
    public static int N;

    // Number of Threads
    static int P;

    // Variable me for Thread Parameter
    int me;

    // Creation of CyclicBarrier used to synchronise data between threads
    static CyclicBarrier cyclicBarrier;

    // Creates variable B, used to determine the amount of 'work' each thread is allocated
    static int B;

    // Creation of the empty two dimensional arrays used to copy data to and from during the fourier transformations
    static double[][] CRe;
    static double[][] CIm;
    static double[][] reconRe;
    static double[][] reconIm;

    // Class constructor
    public Parallel2dFFT(int me) {
        this.me = me;

    }

    public static void main(String[] args) throws Exception {

        N = Integer.parseInt(args[0]);
        P = Integer.parseInt(args[1]);

        B = N/P;
        cyclicBarrier =  new CyclicBarrier(P);
        CRe = new double[N][N];
        CIm = new double[N][N];
        reconRe = new double[N][N];
        reconIm = new double[N][N];

        // Creation of Two Dimensional Array: Column Size - N Row Size - N
        double[][] X = new double[N][N];

        // Reading of the greyscale image - ReadPGM for PGM - readPNG for PNG
        //ReadPGM.read(X, args[2], N);
        readPNG(X, args[2]);

            // Initial Image Output
            DisplayDensity display =
                    new DisplayDensity(X, N, "Original Image");

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

            // Take recording of time at start of runtime
            long startTime = System.currentTimeMillis();

            // Collation of Thread Data
            for (int i = 0; i < P; i++) {
                threads[i].join();
            }

            // Record End Time
            long endTime = System.currentTimeMillis();
            // Output End Time - Start Time
            System.out.println(endTime - startTime);
            // Close application windows upon completion (Uncomment for Testing using Script)
            //System.exit(0);
    }

    // Method to ensure thread-safe processing, will ensure all threads are done processing before continuing.
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

        // FILTERING CODE:
//        int cutoff = N / 16;  // for example
//        for (int k = 0; k < N; k++) {
//            int kSigned = k <= N / 2 ? k : k - N;
//            for (int l = 0; l < N; l++) {
//                int lSigned = l <= N / 2 ? l : l - N;
//                if (Math.abs(kSigned) > cutoff || Math.abs(lSigned) > cutoff) {
//                    CRe[k][l] = 0;
//                    CIm[k][l] = 0;
//                }
//            }
//        }

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

