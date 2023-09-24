package doc.house.dataTypes;

public class Pair<T,N> {
    private T first;
    private N last;
    public Pair(T first, N last){
        this.first = first;
        this.last = last;
    }
    public T getFirst(){
        return this.first;
    }
    public N getLast(){
        return this.last;
    }
}
