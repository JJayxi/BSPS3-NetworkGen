package model.gaenv;

public interface GAEnv {
    public int getSolLen();
    public int eval(int[] sol);
    public boolean verify(int[] sol);
}
