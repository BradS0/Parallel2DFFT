package Lab1;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class FFTImageFiltering {

    public static int N = 256; // Image Size

    public static void main(String[] args) throws Exception {

        double[][] X = new double[N][N]; // Creation of Two Dimensional Array: Column Size - N Row Size - N
        ReadPGM.read(X, "./pics/wolf.pgm", N); // Reading of the greyscale image
        //readPNG(X, "./pics/shib8192.png");

        long startTime = System.currentTimeMillis(); // Take recording of time at start of runtime

        DisplayDensity display =
                new DisplayDensity(X, N, "Original Image"); // Displays the Original image

        // Create array for in-place FFT, and copy original data to it
        double[][] CRe = new double[N][N], CIm = new double[N][N];
        for (int k = 0; k < N; k++) {
            for (int l = 0; l < N; l++) {
                CRe[k][l] = X[k][l];
            }
        }

        // Call to fft2d to perform first Fourier Transform
        fft2d(CRe, CIm, 1);  // Fourier transform

        // Filtering Code - Uncomment to use
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

        Display2dFT display2 =
                new Display2dFT(CRe, CIm, N, "Discrete FT");

        // create array for in-place inverse FFT, and copy FT to it
        double[][] reconRe = new double[N][N],
                reconIm = new double[N][N];
        for (int k = 0; k < N; k++) {
            for (int l = 0; l < N; l++) {
                reconRe[k][l] = CRe[k][l];
                reconIm[k][l] = CIm[k][l];
            }
        }

        fft2d(reconRe, reconIm, -1);  // Inverse Fourier transform

        DisplayDensity display3 =
                new DisplayDensity(reconRe, N, "Reconstructed Image");

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime + "ms");
    }

    static void transpose(double[][] a) {

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                 double current = a[i][j];
                 a[i][j] = a[j][i];
                 a[j][i] = current;
            }
        }
    }

    static void fft2d(double [] [] re, double [] [] im, int isgn) {
        // For simplicity, assume square arrays

        for (int i = 0; i < N; i++){
            FFT.fft1d(re[i], im[i], isgn);
        }

        transpose(re) ;
        transpose(im) ;

        for (int i = 0; i < N; i++){
            FFT.fft1d(re[i], im[i], isgn);
        }

        transpose(re) ;
        transpose(im) ;
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

