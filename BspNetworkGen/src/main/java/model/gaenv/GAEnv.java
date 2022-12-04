package model.gaenv;

public interface GAEnv {
    public int getMaxValue();
    public int getSolLen();
    public int eval(int[] sol);
    public boolean verify(int[] sol);
    public int[] correct(int[] sol);
}
