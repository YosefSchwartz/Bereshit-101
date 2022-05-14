/**
 * This class represents the basic flight controller of the Bereshit space
 * craft.
 *
 * @author ben-moshe
 *
 */
public class Bereshit_101 {
    public static final double WEIGHT_EMP = 165; // kg The weight with empty fuel tank
    public static final double WEIGHT_FUEL = 420; // kg The weight of the fuel
    public static final double WEIGHT_FULL = WEIGHT_EMP + WEIGHT_FUEL; // kg The total weight
    // https://davidson.weizmann.ac.il/online/askexpert/%D7%90%D7%99%D7%9A-%D7%9E%D7%98%D7%99%D7%A1%D7%99%D7%9D-%D7%97%D7%9C%D7%9C%D7%99%D7%AA-%D7%9C%D7%99%D7%A8%D7%97

    public static final double MAIN_ENG_F = 430; // N The force of the main engine in newton units
    public static final double SECOND_ENG_F = 25; // N The force of each secondary engines in newton units

    public static final double MAIN_BURN = 0.15; // liter per sec, 12 liter per m'
    public static final double SECOND_BURN = 0.009; // liter per sec 0.6 liter per m'
    public static final double ALL_BURN = MAIN_BURN + 8 * SECOND_BURN;

    public static double accMax(double weight) {
        return acc(weight, true, 8);
    }

    public static double acc(double weight, boolean main, int seconds_engines) {
        double t = 0;
        if (main) {
            t += MAIN_ENG_F;
        }
        t += seconds_engines * SECOND_ENG_F;
        // t is 430+8*25 = 630
        double ans = t / weight;
        return ans;
    }

    // 14095, 955.5, 24.8, 2.0
    public static void main(String[] args) {
        System.out.println("Simulating Bereshit's Landing:");
        // starting point:
        double verticalSpeed = 24.8;
        double horizontalSpeed = 932;
        double dist = 181 * 1000;
        double angle = 58.3; // zero is vertical (as in landing)
        double altitude = 13748; // 2:25:40 (as in the simulation) // https://www.youtube.com/watch?v=JJ0VfRL9AMs
        double time = 0;
        double deltaTime = 1; // sec
        double acc = 0; // Acceleration rate (m/s^2)
        double fuel = 121; //
        double weight = WEIGHT_EMP + fuel;
        // System.out.println("time, verticalSpeed, hs, dist, alt, ang,weight,acc");
        double NN = 0.7; // rate[0,1]

        // ***** main simulation loop ******
        while (altitude > 0) {
//            if (time % 10 == 0 || altitude < 100) { // print if 100 meters from the moon or time%10 ==0
                System.out.println("time: " + time + "\tverticalSpeed: " + verticalSpeed + "\thorizontalSpeed: "
                        + horizontalSpeed + "\tdist: " + dist + "\taltitude: " + altitude + "\nangle: " + angle
                        + "\tFuel left: " + (weight - WEIGHT_EMP) + "\tacc: " + acc);
                System.out.println("*********************************************************");
//            }

            // over 2 km above the ground
            if (altitude > 2000) { // maintain a vertical speed of [20-25] m/s
                if (verticalSpeed > 25) { // more power for braking
                    NN += 0.003 * deltaTime;
                }
                if (verticalSpeed < 20) { // less power for braking
                    NN -= 0.003 * deltaTime;
                }
                if (altitude > 3500 && altitude < 6000) {
                    angle = 57.0;
                }
                if (altitude > 2000 && altitude < 3500) {
                    angle = 54.0;
                }
            } else { // lower than 2 km - horizontal speed should be close to zero
                if (angle > 3) { // rotate to vertical position.
                    angle -= 3.5;
                } else {
                    angle = 0;
                }

                if (horizontalSpeed < 2) {
                    horizontalSpeed = 0;
                }
                NN = 0.5; // brake slowly, a proper PID controller here is needed!

                if (altitude < 1500 && altitude > 1000) {
                    NN = 0.4;
                }
                if (altitude < 1000 && altitude > 500) {
                    NN = 0.5;
                }
                if (altitude < 500 && altitude > 250) {
                    NN = 0.55;
                }
                if (altitude < 250 && altitude > 125) {
                    NN = 0.7;
                }
                if (altitude < 125) { // very close to the ground!
                    NN = 1; // maximum braking!
                    if (verticalSpeed < 5) { // if it is slow enough - go easy on the brakes
                        NN = 0.2;
                    }
                }
            }
            if (altitude < 5) { // no need to stop
                NN = 0.68;
            }


            // // over 2 km above the ground
            // if(altitude>2000) { // maintain a vertical speed of [20-25] m/s
            // if(verticalSpeed >25) {NN+=0.003*deltaTime;} // more power for braking
            // if(verticalSpeed <20) {NN-=0.003*deltaTime;} // less power for braking
            // }
            // // lower than 2 km - horizontal speed should be close to zero
            // else {
            // if(angle>3) {
            // angle-=3;
            // } // rotate to vertical position.
            // else {
            // angle =0;
            // }
            // NN=0.5; // brake slowly, a proper PID controller here is needed!
            // if(horizontalSpeed<2) {
            // horizontalSpeed=0;
            // }
            // if(altitude<125) { // very close to the ground!
            // NN=1; // maximum braking!
            // if(verticalSpeed<5) {
            // NN=0.7;
            // } // if it is slow enough - go easy on the brakes
            // }
            // }

            // if(altitude<5) { // no need to stop
            // NN=0.4;
            // }

            // main computations
            double ang_rad = Math.toRadians(angle);
            double h_acc = Math.sin(ang_rad) * acc;
            double v_acc = Math.cos(ang_rad) * acc;
            double vacc = Moon.getAcc(horizontalSpeed);
            time += deltaTime;
            double dw = deltaTime * ALL_BURN * NN;
            if (fuel > 0) {
                fuel -= dw;
                weight = WEIGHT_EMP + fuel;
                acc = NN * accMax(weight);
            } else { // ran out of fuel
                acc = 0;
            }

            v_acc -= vacc;
            if (horizontalSpeed > 0) {
                horizontalSpeed -= h_acc * deltaTime;
            }
            dist -= horizontalSpeed * deltaTime;
            verticalSpeed -= v_acc * deltaTime;
            altitude -= deltaTime * verticalSpeed;
        }
    }
}
