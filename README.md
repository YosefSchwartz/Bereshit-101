# Bereshit-101


##### Required JAVA JDK 13.0.1

### How to use
1. Clone this project into your computer
2. Run `Bereshit_101` file

### Explaind
In this task we need to simulate Bereshit spaceship landing.<br />
We needed to improve the landing mode, the initial state is 13748m altitude and 181000m latitude from the expected landing point, and the angle is 58.3.<br />
In our code we divide it to 2 states-<br />
The first state was over 2km alt and we just keep on the vertical speed to be between 20 to 25 m/s^2 and change the angle up to 54.<br />
In the second state, close to the moon, we try to save as we can the fuel and yet land smoothly.<br />
This is reflected by decrease the angle by 3 degrees each delta time and slow down the horizontal speed up to 0.<br />

The main computations calculated by the lecturer.<br />

### By: Eden Shkuri, Shani itzhakov and Yosef Schwartz
