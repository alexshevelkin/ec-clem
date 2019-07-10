package plugins.perrine.easyclemv0.util;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class MatrixUtil {

    public Matrix pseudoInverse(Matrix M) {
        SingularValueDecomposition svd = M.svd();
        Matrix S = svd.getS();
        for(int i = 0; i < S.getRowDimension(); i++) {
            if(S.get(i,i) != 0) {
                S.set(i, i, 1 / S.get(i, i));
            }
        }
        return svd.getV().times(S).times(svd.getU().transpose());
    }
}