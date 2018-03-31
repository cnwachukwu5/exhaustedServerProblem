
import java.util.concurrent.Semaphore;

public class Customer implements Runnable {

    private Semaphore nap;
    private Semaphore servicing;
    private Semaphore door;

    public Customer(){}

    public Customer(Semaphore door, Semaphore nap, Semaphore servicing){
        this.door = door;
        this.nap = nap;
        this.servicing = servicing;
    }

    public synchronized void aboutToEnter(){
        System.out.println(Thread.currentThread().getName() + " attempting to enter the restaurant");
    }

    public synchronized void enter(){
        System.out.println(Thread.currentThread().getName() + " has entered restaurant and is seated");
    }

    public synchronized void waitingTobeServed(){
        try{
            System.out.println(Thread.currentThread().getName() + " is waiting for the Server");
            servicing.acquire();
            System.out.println("Server is servicing " + Thread.currentThread().getName());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public synchronized void served(){
        System.out.println(Thread.currentThread().getName() + " has been served");
    }

    public synchronized void leaving(){
        door.release();
        System.out.println(Thread.currentThread().getName() + " is leaving");
    }

    public void run(){
        aboutToEnter();//Attempt to enter restaurant
        try{

                door.acquire();
                if(door.availablePermits() == 14){//This is first customer
                    nap.release();
                }
                enter();
                waitingTobeServed();
                served();
                leaving();

        }catch(Exception e){
            e.printStackTrace();
        }finally {

        }
    }
}
