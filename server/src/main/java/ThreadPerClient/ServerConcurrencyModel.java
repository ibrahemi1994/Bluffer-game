package ThreadPerClient;
interface ServerConcurrencyModel {
   public void apply (Runnable connectionHandler); 
}