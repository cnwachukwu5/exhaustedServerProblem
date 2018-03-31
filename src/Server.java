import java.util.concurrent.Semaphore;

public class Server implements Runnable{

    private Semaphore nap;
    private Semaphore servicing;
    private Semaphore door;

    public Server(){}

    public Server(Semaphore nap, Semaphore servicing, Semaphore door){
        this.nap = nap;
        this.servicing = servicing;
        this.door = door;
    }
    public void serverSleep(){
        System.out.println("Server is SLEEPING");
    }

    public void serverAwake(){
        System.out.println("Server is now AWAKE");
    }

    public void serve(){
        try{
            Thread.sleep(200); //Simulate recording, and serving order
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            servicing.release();
        }
    }

    public void run(){
        while(true){
            if(!(nap.tryAcquire())) {//if nap semaphore returns false
                try {
                    serverSleep();
                    nap.acquire();
                    serverAwake();
                    while (door.availablePermits() != 15){
                        serve();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    nap.release();
                }
            }

        }
    }
}
