
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {

    //Declare volatile Semaphores so that changes to the variables are visible across all threads
    private static volatile Semaphore door;
    private static volatile Semaphore servicing;
    private static volatile Semaphore nap;

    public static void main(String[] args){
        //Create the Semaphores with various number of permits
        door = new Semaphore(15,true);
        servicing = new Semaphore(0, true);
        nap = new Semaphore(0, true);
        Main mymain = new Main();
        /*
        Generate random number between 50 and 500 to simulate this tread sleep time for off pick period.
        Random integer between 50 - 500 => rand.nextInt(500-50)+ 1 + 50;
         */

        Random rand = new Random();
        Scanner userInput = new Scanner(System.in); //get "Enter" key pressed by user

        //Create two thread-groups to hold related threads for rushHour and slowTime
        ThreadGroup rushHour = new ThreadGroup("RushHour");
        ThreadGroup slowTime = new ThreadGroup("SlowTime");

        Server server = new Server(nap, servicing, door); //Instantiate the Server Class

        Thread[] customerThreads = new Thread[100]; // An array of 100 Customer Threads

        //Assign the first 50 elements of the array to rushHour ThreadGroup
        for(int i = 0; i < 50; i++){
            String name = "Customer " + (i + 1);
            Customer customer = new Customer(door, nap, servicing);
            customerThreads[i] = new Thread(rushHour, customer, name);
        }

        for(int i = 50; i < 100; i++){
            String name = "Customer " + (i + 1);
            Customer customer = new Customer(door, nap, servicing);
            customerThreads[i] = new Thread(slowTime, customer, name);
        }

        //Prompt user to press "enter" key to start simulation of rush hour
        System.out.println("Hit enter to start rush hour simulation");
        String enterKey = userInput.nextLine();
        if(enterKey.isEmpty()){
            Thread serverThread = new Thread(server);
            serverThread.start();
            try{
                Thread.sleep(1000); //Opening the shop
                for(int i =0; i < 50; i++){ //Simulate Rush-hour
                    customerThreads[i].start();
                }
                if(rushHour.activeCount() > 0){
                    Thread.sleep(10000);
                }
                Thread.sleep(1000);

                synchronized (serverThread){
                    serverThread.wait(5);
                }

                System.out.println();
                System.out.println("Hit enter to begin slow time simulation");
                enterKey = userInput.nextLine();

                if(enterKey.isEmpty()){
                    synchronized (serverThread){
                        serverThread.notify();
                    }

                }
                for(int i = 50; i < 100; i++){//Simulate Slow-time
                    int waitTime = rand.nextInt(451) + 50;
                    Thread.sleep(waitTime);
                    customerThreads[i].start();
                }
                if (slowTime.activeCount() > 0){
                    Thread.sleep(10000);
                }
                mymain.terminateThread(serverThread);
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                mymain.terminateThread(serverThread);
            }

        }else{
            System.out.println("Please restart the program and press the enter key");
        }
    }

    public void terminateThread(Thread serverThread){ //Method to terminate.
        try{
            serverThread.join();
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }
}
