package Domain.TransactionControllers;

import Domain.ModelCtrl;

public class TxEliminarAtribut {
    
    private int index;
    
    public TxEliminarAtribut(int index){
        this.index = index;
    }
    
    public void setIndex(int index){
        this.index = index;
    }
    
    public void execute() {
        ModelCtrl mc = ModelCtrl.getInstance();
        
        mc.deleteAttribute(index);
    }
}
