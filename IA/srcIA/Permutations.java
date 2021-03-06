package srcIA;

import java.util.ArrayList;

public class Permutations {
    // a is the original array
    // k is the number of elements in each permutation
    public static ArrayList<ArrayList<Integer>> choose(ArrayList<Integer> a, int k) {
        ArrayList<ArrayList<Integer>> allPermutations = new ArrayList<ArrayList<Integer>>();
        enumerate(a, a.size(), k, allPermutations);
        return allPermutations;
    }

    // a is the original array
    // n is the array size
    // k is the number of elements in each permutation
    // allPermutations is all different permutations
    private static void enumerate(ArrayList<Integer> a, int n, int k, ArrayList<ArrayList<Integer>> allPermutations) {
        if (k == 0) {
            ArrayList<Integer> singlePermutation = new ArrayList<Integer>();
            for (int i = n; i < a.size(); i++) {
                singlePermutation.add(a.get(i));
            }
            allPermutations.add(singlePermutation);
            return;
        }

        for (int i = 0; i < n; i++) {
            swap(a, i, n - 1);
            enumerate(a, n - 1, k - 1, allPermutations);
            swap(a, i, n - 1);
        }
    }

    // helper function that swaps a.get(i) and a.get(j)
    public static void swap(ArrayList<Integer> a, int i, int j) {
        Integer temp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, temp);
    }


    // sample client
    public static void main(String[] args) {

        // n is the end item of the array.
        // if n = 5, the array is [0, 1, 2, 3, 4, 5]
        // k is the number of elements of each permutation.
        int n = 5;
//        int k = 2;

        // create original array
        ArrayList<Integer> elements = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            elements.add(i);
        }

//        ArrayList<Integer> a = new ArrayList<>();
//        for (int i = 0; i < n; i++) {
//            a.add(elements.get(i));
//        }

//        System.out.println(elements);
//        System.out.println(a);

        // Output full list
        ArrayList<ArrayList<Integer>> OutputList = new ArrayList();
        for (int k = 1; k <= n; k++) {
            OutputList.addAll(choose(elements, k));
        }
        System.out.println(elements);
        System.out.println(OutputList);
    }
}
