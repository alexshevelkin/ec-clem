package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.transformation.AffineTransformation;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AffineTransformationComputerTest {
    private AffineTransformationComputer subjectUnderTest = new AffineTransformationComputer();

    @Test
    void simpleRotation() {
        List<Point> sourcePoints = new ArrayList<>();
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }, { 2 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }, { 3 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        Dataset source = new Dataset(sourcePoints);

        List<Point> targetPoints = new ArrayList<>();
        targetPoints.add(new Point(new Matrix(new double[][] {{ -4 }, { 2 }, { 2 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -4 }, { -2 }, { 4 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{  4 }, { 0 }, { 6 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -4 }, { 2 }, { 6 }})));
        Dataset target = new Dataset(targetPoints);

        AffineTransformation result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(4, result.getMatrix().getRowDimension());
        assertEquals(4, result.getMatrix().getColumnDimension());
        assertEquals(0, result.getMatrix().get(0, 0), 0.0000000001);
        assertEquals(2, result.getMatrix().get(1, 0), 0.0000000001);
        assertEquals(0, result.getMatrix().get(2, 0), 0.0000000001);
        assertEquals(0, result.getMatrix().get(3, 0), 0.0000000001);
        assertEquals(-2, result.getMatrix().get(0, 1), 0.0000000001);
        assertEquals(0, result.getMatrix().get(1, 1), 0.0000000001);
        assertEquals(0, result.getMatrix().get(2, 1), 0.0000000001);
        assertEquals(0, result.getMatrix().get(3, 1), 0.0000000001);
        assertEquals(0, result.getMatrix().get(0, 2), 0.0000000001);
        assertEquals(0, result.getMatrix().get(1, 2), 0.0000000001);
        assertEquals(2, result.getMatrix().get(2, 2), 0.0000000001);
        assertEquals(0, result.getMatrix().get(3, 2), 0.0000000001);
        assertEquals(0, result.getMatrix().get(0, 3), 0.0000000001);
        assertEquals(0, result.getMatrix().get(1, 3), 0.0000000001);
        assertEquals(0, result.getMatrix().get(2, 3), 0.0000000001);
        assertEquals(1, result.getMatrix().get(3, 3), 0.0000000001);
    }

    @Test
    void simpleTranslation() {
        List<Point> sourcePoints = new ArrayList<>();
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }, { 2 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }, { 3 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        Dataset source = new Dataset(sourcePoints);

        List<Point> targetPoints = new ArrayList<>();
        targetPoints.add(new Point(new Matrix(new double[][] {{ 2 }, { 3 }, { 2 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 0 }, { 3 }, { 3 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 1 }, { -1 }, { 4 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 2 }, { 3 }, { 4 }})));
        Dataset target = new Dataset(targetPoints);

        AffineTransformation result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(4, result.getMatrix().getRowDimension());
        assertEquals(4, result.getMatrix().getColumnDimension());
        assertEquals(1, result.getMatrix().get(0, 0), 0.0000000001);
        assertEquals(0, result.getMatrix().get(1, 0), 0.0000000001);
        assertEquals(0, result.getMatrix().get(2, 0), 0.0000000001);
        assertEquals(0, result.getMatrix().get(3, 0), 0.0000000001);
        assertEquals(0, result.getMatrix().get(0, 1), 0.0000000001);
        assertEquals(1, result.getMatrix().get(1, 1), 0.0000000001);
        assertEquals(0, result.getMatrix().get(2, 1), 0.0000000001);
        assertEquals(0, result.getMatrix().get(3, 1), 0.0000000001);
        assertEquals(0, result.getMatrix().get(0, 2), 0.0000000001);
        assertEquals(0, result.getMatrix().get(1, 2), 0.0000000001);
        assertEquals(1, result.getMatrix().get(2, 2), 0.0000000001);
        assertEquals(0, result.getMatrix().get(3, 2), 0.0000000001);
        assertEquals(1, result.getMatrix().get(0, 3), 0.0000001);
        assertEquals(1, result.getMatrix().get(1, 3), 0.0000000001);
        assertEquals(1, result.getMatrix().get(2, 3), 0.0000000001);
        assertEquals(1, result.getMatrix().get(3, 3), 0.0000000001);
    }

    @Test
    void complexRotation() {
        List<Point> sourcePoints = new ArrayList<>();
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }, { 2 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }, { 3 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        Dataset source = new Dataset(sourcePoints);

        List<Point> targetPoints = new ArrayList<>();
        targetPoints.add(new Point(new Matrix(new double[][] {{ -2.1213204 }, { 1 }, { -0.7071068 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -2.8284272 }, { -1 }, { 0 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -0.7071068 }, { 0 }, { 3.5355340 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -3.5355340 }, { 1 }, { 0.7071068 }})));
        Dataset target = new Dataset(targetPoints);

        AffineTransformation result = subjectUnderTest.compute(new FiducialSet(source, target));
        result.getMatrix().print(1,5);
        assertEquals(4, result.getMatrix().getRowDimension());
        assertEquals(4, result.getMatrix().getColumnDimension());
        assertEquals(0, result.getMatrix().get(0, 0), 0.0000001);
        assertEquals(1, result.getMatrix().get(1, 0), 0.0000001);
        assertEquals(0, result.getMatrix().get(2, 0), 0.0000001);
        assertEquals(0, result.getMatrix().get(3, 0), 0.0000001);
        assertEquals(-0.7071068, result.getMatrix().get(0, 1), 0.0000001);
        assertEquals(0, result.getMatrix().get(1, 1), 0.0000001);
        assertEquals(-0.7071068, result.getMatrix().get(2, 1), 0.0000001);
        assertEquals(0, result.getMatrix().get(3, 1), 0.0000001);
        assertEquals(-0.7071068, result.getMatrix().get(0, 2), 0.0000001);
        assertEquals(0, result.getMatrix().get(1, 2), 0.0000001);
        assertEquals(0.7071068, result.getMatrix().get(2, 2), 0.0000001);
        assertEquals(0, result.getMatrix().get(3, 2), 0.0000001);
        assertEquals(0, result.getMatrix().get(0, 3), 0.0000001);
        assertEquals(0, result.getMatrix().get(1, 3), 0.0000001);
        assertEquals(0, result.getMatrix().get(2, 3), 0.000001);
        assertEquals(1, result.getMatrix().get(3, 3), 0.000001);
    }
}