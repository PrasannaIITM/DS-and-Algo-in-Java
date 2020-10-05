public class UnionFind {
    private int size;//number of elements 
    private int[] sz; //sizes of each of the components

    //id[i] points to the parent of i, if id[i] is a root node
    private int[] id;

    private int numComponents;

    public UnionFind(int size){
        if(size <= 0) throw new IllegalArgumentException("Size <= 0 is not allowed");

        this.size = numComponents = size;
        sz = new int[size];
        id = new int[size];

        for(int i = 0; i < size; i++){
            id[i] = i;
            sz[i] = 1;
        }
    }

    public int find(int p){
        int root = p;
        while(root != id[root]){
            root = id[root];
        }

        //path compression
        int next;
        while(p != root){
            next = id[p];//find the parent which is not root
            id[p] = root;//set parent to the root
            p = next;//p = the intermediate parent
        }

        return root;
    }

    public boolean connected(int p, int q){
        return find(p) == find(q);
    }

    public int componentSize(int p){
        return sz[find(p)];
    }

    public int size(){
        return size;
    }

    public void unify(int p, int q){
        int root1 = find(p);
        int root2 = find(q);

        if(root1 == root2) return;
        //merge smaller component into bigger one
        if(sz[root1] < sz[root2]){
            sz[root2] += sz[root1];
            id[root1] = root2;
        }else{
            sz[root1] += sz[root2];
            id[root2] = root1;
        }
        numComponents--;
    }

    


}
