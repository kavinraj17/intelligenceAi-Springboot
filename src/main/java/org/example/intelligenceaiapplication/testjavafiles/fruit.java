package org.example.intelligenceaiapplication.testjavafiles;

class fruit{
    public static int unPlaced(int []fruit,int[] basket)
    {
        int fSize = fruit.length;
        boolean [] used = new boolean[fSize];
        int unPlaced = 0;
        for(int f:fruit)
        {
            boolean placed = false;
            for(int j = 0; j<fSize; j++)
            {
                if(!used[j] && basket[j]>=f)
                {
                    used[j] = true;
                    placed = true;
                    break;
                }
            }
            if(!placed)
                unPlaced++;
        }
        return unPlaced;
    }
    public static void main(String args[])
    {
        int[] fruit = {3,6,8};
        int[] basket = {3,1,6};

        System.out.println(unPlaced(fruit,basket));
    }
}