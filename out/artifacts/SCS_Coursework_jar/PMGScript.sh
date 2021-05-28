echo "-------------------256------------------"
echo "TESTING 256 2"
for i in {1..10}
do
java -jar "SCS Coursework.jar" 256 2 ./pics/wolf.pgm
done

echo "TESTING 256 4"
for i in {1..10}
do
java -jar "SCS Coursework.jar" 256 4 ./pics/wolf.pgm
done

echo "TESTING 256 8"
for i in {1..10}
do
java -jar "SCS Coursework.jar" 256 8 ./pics/wolf.pgm
done

echo "TESTING 256 16"
for i in {1..10}
do
java -jar "SCS Coursework.jar" 256 16 ./pics/wolf.pgm
done

echo "-------------------512------------------"
echo "TESTING 512 2"
for i in {1..10}
do
java -jar "SCS Coursework.jar" 512 2 ./pics/lena.ascii.pgm
done

echo "TESTING 512 4"
for i in {1..10}
do
java -jar "SCS Coursework.jar" 512 4 ./pics/lena.ascii.pgm
done

echo "TESTING 512 8"
for i in {1..10}
do
java -jar "SCS Coursework.jar" 512 8 ./pics/lena.ascii.pgm
done

echo "TESTING 512 16"
for i in {1..10}
do
java -jar "SCS Coursework.jar" 512 16 ./pics/lena.ascii.pgm
done