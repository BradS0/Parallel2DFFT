Using thread parallelism in Java, make a parallel version of the 2-dimensional Fast Fourier Transform developed in the week 2 practical session. Note that you do not need to parallelize or modify the 1D FFT operation to achieve this.

Benchmark your parallel program for different numbers of threads on a suitable dataset - you may start with the "wolf" image introduced in practical 1. Preferably, repeat this for a series of datasets. You may need to consider larger monochrome input images for successful parallelization. Here is a crude way to read an image density array from a monochrome PNG file:

import java.io.File ;

import java.awt.image.Raster ;
import java.awt.image.BufferedImage ;

import javax.imageio.ImageIO ;

[...]
public static int N = 400 ;  // or whatever

[...]
double [] [] density = new double [N] [N] ;

BufferedImage img = ImageIO.read(new File("some-file.png"));  // or whatever

Raster ras = img.getData() ;
for (int i = 0 ; i < N ; i++) {
for (int j = 0 ; j < N ; j++) {
density [i] [j] = ras.getSample(i, j, 0) ;
}
}