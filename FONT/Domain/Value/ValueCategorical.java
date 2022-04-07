// Author: Pol Rivero

package Domain.Value;
import Exceptions.ImplementationError;
import java.util.Arrays;

import Domain.Value.DerivedAttribute.CategoricalOperator;
import Domain.Value.DerivedAttribute.Operator;

import java.util.ArrayList;

public class ValueCategorical extends Value {
    
    // Regex for capturing the CSV separator between 2 tags
    private static final String SEPARATOR_REGEX = ";";
    
    String[] val;
    String originalString;
    
    // Warning: val is modified
    public ValueCategorical(Attribute attribute, String tags) {
        super(attribute);
        originalString = tags;
        
        if (attribute.getClass() != AttributeCategorical.class)
        throw new ImplementationError("Creating categorical value on a non-categorical attribute");
        
        // Get tags
        String[] input = tags.split(SEPARATOR_REGEX);
        
        // Manipulate the tags to make them easier to compare in the future
        
        // Sort the array
        Arrays.sort(input);
        
        // Remove repeated values and convert to lowercase
        String lastStr = "";
        ArrayList<String> temp = new ArrayList<String>(input.length);
        for (String s : input) {
            boolean repeated = (s.compareTo(lastStr) == 0);
            if (!repeated) temp.add(s.toLowerCase());
            lastStr = s;
        }
        
        this.val = new String[temp.size()];
        this.val = temp.toArray(this.val);
    }
    
    // Pre: other is a ValueCategorical
    protected double normalizedDistance(Value other) {        
        String[] otherVal = ((ValueCategorical) other).val;
        
        int[] unionIntersection = getUnionIntersectionSizes(val, otherVal);
        
        double hits = unionIntersection[1];  // Intersection
        double total = unionIntersection[0]; // Union
        
        return hits / total;
    }
    
    
    
    // Returns a pair of integers, representing the size of the union (index 0) and intersection (index 1)
    // Pre: there are no duplicates in any of the arrays
    private static int[] getUnionIntersectionSizes(String[] A, String[] B) {
        int unionSize = 0;
        int intesectionSize = 0;
        int i = 0;
        int j = 0;
        
        while (i < A.length && j < B.length) {
            int comparison = A[i].compareTo(B[j]);
            unionSize++; // Always increment union
            
            if (comparison < 0) i++;        // A is lexicographically less than B
            else if (comparison > 0) j++;   // B is lexicographically less than A
            else {  // Strings are equal
                intesectionSize++;
                i++;
                j++;
            }
        }
        
        // Count the remaining elements of both arrays
        unionSize += A.length - i;
        unionSize += B.length - j;
        
        return new int[] {unionSize, intesectionSize};
    }
    
    protected Double evaluateOperator(Operator op) {
        CategoricalOperator cOp;
        try {
            cOp = (CategoricalOperator) op;
        }
        catch (ClassCastException e) {
            throw new ImplementationError("Trying to use a non-categorical operator on a categorical attribute");
        }
        return cOp.evaluate(val);
    }
    
    public String toString() {
        return originalString;
    }
}
