# mergea4toa3

Thank you for coming by, this is a GUI program for merging DIN A4 scanned fragments to obtain a single DIN A3 image, very useful when you don't have an DIN A3 specific scanner, but a DIN A4 one.

This program specifically:
 * Can join three images that are parts of the same image (i.e. left, center, and right size of the DIN A3 landscape image, all in DIN A4 portrait size).
 * Provides full sized images that are a merge from the previous three images.
 * Offers an user-friendly GUI environment to do that.

This program is actually not limited to A4 to A3 workflows, but can also use different image sizes provided that, without having to reduce the size of the image, all the used fragments can be overlapped and match, obtaining a final, merged image.

This program uses the `libmergea4toa3` library, which you can find here: [https://github.com/davovoid/libmergea4toa3](https://github.com/davovoid/libmergea4toa3).

### How to compile the project

The project is based on Java and Maven, so a Maven-friendly environment would be desired.

* First of all, you need to install the `libmergea4toa3` library into your Maven environment. Please refer to the instructions from the README available on [https://github.com/davovoid/libmergea4toa3](https://github.com/davovoid/libmergea4toa3).

* Download the `mergea4toa3` project from this GitHub repository or execute the following command using git:

```
git clone https://github.com/davovoid/mergea4toa3.git
```

* Execute the `clean compile assembly:single` goal from your favorite Maven-friendly IDE or using the following Maven command:

```
mvn clean compile assembly:single
```

* After a success build from the Maven command line, a `mergea4toa3-VERSION-jar-with-dependencies.jar` file should have been generated in the `target` folder inside the project. This is the final executable, which already contains all the libraries required by the program, and thus it can be just copied, pasted, executed or distributed with no more requirements than the JRE itself.

### Execution issues, hangs and so

In case of merging big images (i.e. DIN A4 @ >300ppp), the program might run out of memory with default JRE configuration, so here is a...

### Recommended way of executing the program

This is my personal favorite way of executing the program:

 * Avoid using Java 8. Currently the program is being tested in OpenJDK 13, and from my point of view it is way better at dealing with high window scaling (i.e. HiDPI, 2K, 4K and so). The current Java 8 version I have would pixelate the window content, giving you a worse experience.
 
 * In any case, the JRE should execute the application with around 2GiB maximum memory, which can be done by executing the following command:

Windows

```
java.exe -Xmx2048m -jar path_to_the_mergea4toa3.jar
```

Linux

```
java -Xmx2048m -jar path_to_the_mergea4toa3.jar
```

This would also give you the advantage of watching the console output when an error occurs, very important in the current program development stage (very early).