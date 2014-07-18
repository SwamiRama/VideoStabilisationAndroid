package videoStabilisation.motionEstimation;

import java.util.ArrayList;

import org.opencv.core.Point;

import Jama.Matrix;



public class ParameterExtraction {
	private boolean isSmoothing;
	private double Skewing;
	private double Scaling;
	private double ScalingX;

	private boolean islocked;
	double Rotation;
	private Point[] Centroid;
	double TranslationX;
	double TranslationY;
	double A11;
	double A12;
	double A21;
	double A22;

	public ParameterExtraction(boolean isSmoothing,
			Point[][] bestMatchingPoints, Point center) {

		this.isSmoothing = isSmoothing;
		if (bestMatchingPoints == null || bestMatchingPoints.length == 0) {
			bestMatchingPoints = new Point[1][];
			bestMatchingPoints[0] = new Point[2];
			bestMatchingPoints[0][0] = new Point(0, 0);
			bestMatchingPoints[0][1] = new Point(0, 0);
		} else {
			// Point[] pts1 = new Point[bestMatchingPoints.length];
			// Point[] pts2 = new Point[bestMatchingPoints.length];

			// for (int i = 0; i < bestMatchingPoints.length; i++)
			// {
			// pts1[i] = new Point(bestMatchingPoints[i][0].x,
			// bestMatchingPoints[i][0].y);
			// pts2[i] = new Point(bestMatchingPoints[i][1].x,
			// bestMatchingPoints[i][1].y);
			// }
			// Homography = CameraCalibration.GetPerspectiveTransform(pts1,
			// pts2);
			double[] first = new double[3];
			double[] second = new double[3];
			double[] third = new double[3];
			double[] fourth = new double[3];
			ArrayList<Point[]> firstList = new ArrayList<Point[]>();
			ArrayList<Point[]> secondList = new ArrayList<Point[]>();
			ArrayList<Point[]> thirdList = new ArrayList<Point[]>();
			ArrayList<Point[]> fourthList = new ArrayList<Point[]>();
			for (int i = 0; i < bestMatchingPoints.length; i++) {
				// FIRST
				if (bestMatchingPoints[i][0].x > center.x
						&& bestMatchingPoints[i][1].x > center.x
						&& bestMatchingPoints[i][0].y < center.y
						&& bestMatchingPoints[i][1].y < center.y)
					firstList.add(bestMatchingPoints[i]);
				// SECOND
				if (bestMatchingPoints[i][0].x < center.x
						&& bestMatchingPoints[i][1].x < center.x
						&& bestMatchingPoints[i][0].y < center.y
						&& bestMatchingPoints[i][1].y < center.y)
					secondList.add(bestMatchingPoints[i]);
				if (bestMatchingPoints[i][0].x < center.x
						&& bestMatchingPoints[i][1].x < center.x
						&& bestMatchingPoints[i][0].y > center.y
						&& bestMatchingPoints[i][1].y > center.y)
					thirdList.add(bestMatchingPoints[i]);
				if (bestMatchingPoints[i][0].x > center.x
						&& bestMatchingPoints[i][1].x > center.x
						&& bestMatchingPoints[i][0].y > center.y
						&& bestMatchingPoints[i][1].y > center.y)
					fourthList.add(bestMatchingPoints[i]);
			}
			double s = calcS(bestMatchingPoints);
			switch (4) {
			case 0:
				if (firstList.size() > 2)
					first = extractionNorm((Point[][]) firstList.toArray(), s);
				if (firstList.size() > 2)
					second = extractionNorm((Point[][]) firstList.toArray(), s);
				if (firstList.size() > 2)
					third = extractionNorm((Point[][]) firstList.toArray(), s);
				if (firstList.size() > 2)
					fourth = extractionNorm((Point[][]) firstList.toArray(), s);

				Rotation = (first[2] + second[2] + third[2] + fourth[2]) / 4;
				TranslationX = (first[0] + second[0] + third[0] + fourth[0]) / 4;
				TranslationY = (first[1] + second[1] + third[1] + fourth[1]) / 4;
				break;
			case 1:
				if (firstList.size() > 2)
					first = extractionALL((Point[][]) (Point[][]) firstList
							.toArray());
				if (firstList.size() > 2)
					second = extractionALL((Point[][]) firstList.toArray());
				if (firstList.size() > 2)
					third = extractionALL((Point[][]) firstList.toArray());
				if (firstList.size() > 2)
					fourth = extractionALL((Point[][]) firstList.toArray());

				Rotation = -(first[2] + second[2] + third[2] + fourth[2]) / 4;
				TranslationX = -(first[0] + second[0] + third[0] + fourth[0]) / 4;
				TranslationY = -(first[1] + second[1] + third[1] + fourth[1]) / 4;
				break;
			case 2:

				first = extractionNorm(bestMatchingPoints, s);
				Rotation = first[2];
				TranslationX = first[0];
				TranslationY = first[1];
				break;
			case 3:
				calcCentroid(bestMatchingPoints);
				Rotation = newRotation(bestMatchingPoints, center);
				newTranslation(bestMatchingPoints);
				break;
			// Paper: Low Complexity Global Motion Estimation from Block Motion
			// Vectors
			case 4:
				Scaling = s;
				double VFX2quad = sumValues(bestMatchingPoints, 0, true, true);
				double VFY2quad = sumValues(bestMatchingPoints, 0, false, true);
				double VFX2 = sumValues(bestMatchingPoints, 0, true, false);
				double VFY2 = sumValues(bestMatchingPoints, 0, false, false);
				double VFX1 = sumValues(bestMatchingPoints, 1, true, false);
				double VFY1 = sumValues(bestMatchingPoints, 1, false, false);
				double K = bestMatchingPoints.length;
				first = extractionNorm(bestMatchingPoints, s);
				Rotation = first[2];
				// Rotation = calcRotation(VFX2quad, VFY2quad, VFX2, VFY2, VFX1,
				// VFY1, K, s);
				TranslationX = calcTranslationX(VFX2, VFX1, VFY1, K, s,
						Rotation);
				TranslationY = calcTranslationY(VFY2, VFY1, K, s, Rotation);
				break;
			}
			// switch (bestMatchingPoints.length)
			// {
			// case 0:
			// TranslationX = 0;
			// TranslationY = 0;
			// Rotation = 0;
			// break;
			// case 1:
			// Rotation = 0;
			// TranslationX = (bestMatchingPoints[0][0].x -
			// bestMatchingPoints[0][1].x);
			// TranslationY = (bestMatchingPoints[0][0].y -
			// bestMatchingPoints[0][1].y);
			// break;
			// case 2:
			// Rotation = 0;
			// TranslationX = (bestMatchingPoints[0][0].x -
			// bestMatchingPoints[0][1].x);
			// TranslationY = (bestMatchingPoints[0][0].y -
			// bestMatchingPoints[0][1].y);
			// break;
			// default:
			// //calcCentroid(bestMatchingPoints);
			// //newTranslation(bestMatchingPoints);
			// //newRotation(bestMatchingPoints, center);
			// extractionNorm(bestMatchingPoints, center);
			// //extractionALL(bestMatchingPoints);
			// break;
			// }
		}

	}

	private double calcTranslationY(double VFY2, double VFY1, double K,
			double s, double r) {
		return (1 / K)
				* (VFY2 - s * Math.sin(r) * VFY1 - s * Math.cos(r) * VFY1);
	}

	private double calcTranslationX(double VFX2, double VFX1, double VFY1,
			double K, double s, double r) {
		return (1 / K)
				* (VFX2 - s * Math.cos(r) * VFX1 + s * Math.sin(r) * VFY1);
	}

	private double calcS(Point[][] bestMatchingPoints) {
		if (bestMatchingPoints.length > 1) {
			double first = Math.pow(bestMatchingPoints[1][1].x
					- bestMatchingPoints[0][1].x, 2);
			double second = Math.pow(bestMatchingPoints[1][1].y
					- bestMatchingPoints[0][1].y, 2);
			double third = Math.pow(bestMatchingPoints[1][0].x
					- bestMatchingPoints[0][0].x, 2);
			double fourth = Math.pow(bestMatchingPoints[1][0].y
					- bestMatchingPoints[0][0].y, 2);
			return Math.sqrt((first + second) / (third + fourth));
		} else {
			return 1;
		}
	}

	private double calcRotation(double VFX2quad, double VFY2quad, double VFX2,
			double VFY2, double VFX1, double VFY1, double K, double s) {
		double firstAcos = K * VFX2quad + K * VFY2quad - Math.pow(VFX2, 2)
				- Math.pow(VFY2, 2);
		double FirstTerm = Math.acos(firstAcos);
		double secondArcos = s
				* (VFX1 * VFX2 + VFX1 * VFY2 - VFY1 * VFX2 + VFY1 * VFY2 - (-K)
						* VFX1 * VFX2 - K * VFX1 * VFY2 + K * VFX1 * VFY2 - K + VFY1
						* VFY2);
		double SecondTerm = Math.acos(secondArcos);
		double winkel = FirstTerm + SecondTerm;
		// if (firstAcos == double.NaN)
		// {
		// return SecondTerm;
		// }
		// if (secondArcos == double.NaN)
		// {
		// return FirstTerm;
		// }
		// if (winkel == double.NaN)
		// {
		// return 0;
		// }
		// else
		// {
		return winkel;
		// }
	}

	private double sumValues(Point[][] points, int srcOrDst, boolean isXValue,
			boolean isQuad) {
		double sum = 0;
		if (isXValue) {
			for (int i = 0; i < points.length; i++) {
				if (isQuad) {
					sum += Math.pow(points[i][srcOrDst].x, 2);
				} else {
					sum += points[i][srcOrDst].x;
				}
			}
		} else {
			for (int i = 0; i < points.length; i++) {
				if (isQuad) {
					sum += Math.pow(points[i][srcOrDst].x, 2);
				} else {
					sum += points[i][srcOrDst].y;
				}
			}
		}
		return sum;
	}

	private void calcCentroid(Point[][] bestMatchingPoints) {
		double x_Value_src = 0;
		double y_Value_src = 0;
		double x_Value_dst = 0;
		double y_Value_dst = 0;
		double length = bestMatchingPoints.length;
		for (int i = 0; i < length; i++) {
			x_Value_src += bestMatchingPoints[i][0].x;
			y_Value_src += bestMatchingPoints[i][0].y;
			x_Value_dst += bestMatchingPoints[i][1].x;
			y_Value_dst += bestMatchingPoints[i][1].y;
		}
		Centroid = new Point[2];
		Centroid[0] = new Point((float) (x_Value_src / length),
				(float) (y_Value_src / length));
		Centroid[1] = new Point((float) (x_Value_dst / length),
				(float) (y_Value_dst / length));
	}

	private double[] extractionALL(Point[][] bestMatchingPoints) {
		double[] velues = new double[3];
		if (bestMatchingPoints.length > 2) {
			double[][] A = new double[bestMatchingPoints.length * 2][];
			for (int i = 0, j = 0; i < bestMatchingPoints.length; i++) {
				if (i < 3) {
					A[i] = new double[6];
					A[i + 3] = new double[6];
					A[i] = new double[] { bestMatchingPoints[i][0].x,
							bestMatchingPoints[i][0].y, 1, 0, 0, 0 };
					A[i + 3] = new double[] { 0, 0, 0,
							bestMatchingPoints[i][0].x,
							bestMatchingPoints[i][0].y, 1 };
				} else {
					A[i + 3 + j] = new double[6];
					A[i + 3 + j] = new double[] { bestMatchingPoints[i][0].x,
							bestMatchingPoints[i][0].y, 1, 0, 0, 0 };
					A[i + 4 + j] = new double[6];
					A[i + 4 + j] = new double[] { 0, 0, 0,
							bestMatchingPoints[i][0].x,
							bestMatchingPoints[i][0].y, 1 };
					j++;
				}
			}

			double[] b = new double[bestMatchingPoints.length * 2];
			for (int i = 0, j = 0; i < bestMatchingPoints.length; i++) {
				if (i < 3) {
					b[i] = bestMatchingPoints[i][1].x;
					b[i + 3] = bestMatchingPoints[i][1].y;
				} else {
					b[i + 3 + j] = bestMatchingPoints[i][1].x;
					b[i + 4 + j] = bestMatchingPoints[i][1].y;
					j++;
				}
			}

			Matrix A_Matrix = new Matrix(A);
			Matrix b_Matrix = new Matrix(b, 1);
			Matrix A_Transpose = A_Matrix.transpose();

			if ((A_Transpose.times(A_Matrix)).det() != 0) {
				Matrix x_Matrix = ((A_Transpose.times(A_Matrix)).inverse())
						.times(A_Transpose).times(b_Matrix.transpose());

				double[] x = new double[6];
				x = x_Matrix.getColumnPackedCopy();

				A11 = x[0];
				A12 = x[1];
				velues[0] = x[2];
				A21 = x[3];
				A22 = x[4];
				velues[1] = x[5];
				velues[2] = calcMoreParameter(A11, A12, A21, A22);
			} else {
				velues[2] = 0;
				velues[0] = 0;
				velues[1] = 0;
			}
			return velues;
		} else {
			// extractionNorm(bestMatchingPoints);
		}
		return velues;
	}

	private double[] extractionNorm(Point[][] bestMatchingPoints, double s) {
		double[] values = new double[3];
		double[][] A = new double[bestMatchingPoints.length * 2][];
		for (int i = 0, j = 0; i < bestMatchingPoints.length * 2; i++) {
			A[i] = new double[3];
			if (i % 2 == 0) {
				A[i][0] = s * bestMatchingPoints[j][1].y;
				A[i][1] = 1;
				A[i][2] = 0;
			} else {
				A[i][0] = -s * bestMatchingPoints[j][1].x;
				A[i][1] = 0;
				A[i][2] = 1;
				j++;
			}
		}

		double[] b = new double[bestMatchingPoints.length * 2];
		for (int i = 0, j = 0; i < b.length; i++) {
			if (i % 2 == 0) {
				b[i] = bestMatchingPoints[j][0].x - s
						* bestMatchingPoints[j][1].x;
			} else {
				b[i] = bestMatchingPoints[j][0].y - s
						* bestMatchingPoints[j][1].y;
				j++;
			}
		}

		Matrix matrix_A = new Matrix(A);
		Matrix matrix_b = new Matrix(b, 1);
		Matrix transpose_A = matrix_A.transpose();

		if ((transpose_A.times(matrix_A)).det() != 0) {
			Matrix x_Matrix = ((transpose_A.times(matrix_A)).inverse()).times(
					transpose_A).times(matrix_b.transpose());

			double[] x = new double[3];
			x = x_Matrix.getColumnPackedCopy();

			values[0] = x[1];// x
			values[1] = x[2];// y
			values[2] = x[0];// rotation

		} else {
			values[2] = 0;
			values[0] = (bestMatchingPoints[0][0].x - bestMatchingPoints[0][1].x);
			values[1] = (bestMatchingPoints[0][0].y - bestMatchingPoints[0][1].y);
		}
		return values;
	}

	private void newTranslation(Point[][] bestMatchingPoints) {
		double translation_x = 0;
		double translation_y = 0;
		for (int i = 0; i < bestMatchingPoints.length; i++) {
			translation_x += (bestMatchingPoints[0][0].x - bestMatchingPoints[0][1].x);
			translation_y += (bestMatchingPoints[0][0].y - bestMatchingPoints[0][1].y);
		}
		TranslationX = translation_x / (bestMatchingPoints.length);
		TranslationY = translation_y / (bestMatchingPoints.length);
	}

	private double newRotation(Point[][] bestMatchingPoints, Point center) {
		// double rotation = 0;
		// double directionOfRotation = 0;
		// int leftRotationCounter = 0;
		// int rightRotationCounter = 0;
		// for (int i = 0; i < bestMatchingPoints.length; i++)
		// {
		// double xtrash = bestMatchingPoints[i][0].x -
		// bestMatchingPoints[i][1].x;
		// double ytrash = bestMatchingPoints[i][0].y -
		// bestMatchingPoints[i][1].y;
		// if (ytrash == 0 || ytrash == 0)
		// {
		// rotation += 0;
		// }
		// else
		// {
		// if ((bestMatchingPoints[i][0].x > bestMatchingPoints[i][1].x &&
		// bestMatchingPoints[i][0].y < center.y && bestMatchingPoints[i][1].y <
		// center.y)
		// ||
		// (bestMatchingPoints[i][0].x < bestMatchingPoints[i][1].x &&
		// bestMatchingPoints[i][0].y > center.y && bestMatchingPoints[i][1].y >
		// center.y)
		// ||
		// (bestMatchingPoints[i][0].y < bestMatchingPoints[i][1].y &&
		// bestMatchingPoints[i][0].x < center.x && bestMatchingPoints[i][1].x <
		// center.y)
		// ||
		// (bestMatchingPoints[i][0].y > bestMatchingPoints[i][1].y &&
		// bestMatchingPoints[i][0].x > center.x && bestMatchingPoints[i][1].x >
		// center.y))
		// {
		// directionOfRotation = -1;
		// leftRotationCounter++;
		// }
		// else
		// {
		// directionOfRotation = 1;
		// rightRotationCounter++;
		// }
		// if ((leftRotationCounter == 0 && rightRotationCounter > 0) ||
		// rightRotationCounter == 0 && leftRotationCounter > 0)
		// {
		// double a = ValueCalculation.distance(bestMatchingPoints[i]);
		// double b = ValueCalculation.distance(new Point[] {
		// bestMatchingPoints[i][1], center });
		// double c = ValueCalculation.distance(new Point[] {
		// bestMatchingPoints[i][0], center });
		// double alpha_zaehler = b * b + c * c - a * a;
		// double alpha_nenner = 2 * b * c;
		// double beta_zaehler = a * a + c * c - b * b;
		// double beta_nenner = 2 * a * c;
		// double gamma_zaehler = a * a + b * b - c * c;
		// double gamma_nenner = 2 * a * b;
		// double alpha_value = alpha_zaehler / alpha_nenner;
		// double beta_vlaue = beta_zaehler / beta_nenner;
		// double gamma_value = gamma_zaehler / gamma_nenner;

		// if (beta_nenner == 0 || gamma_nenner == 0 || alpha_nenner == 0 ||
		// Math.Abs(alpha_value) > 1 || Math.Abs(beta_vlaue) > 1 ||
		// Math.Abs(gamma_value) > 1)
		// {
		// rotation += 0;
		// }
		// else
		// {
		// double alpha = Math.acos(alpha_value);
		// double beta = Math.acos(beta_vlaue);
		// double gamma = Math.acos(gamma_value);
		// if (alpha < beta)
		// {
		// if (alpha < gamma)
		// {
		// rotation = alpha * directionOfRotation;
		// }
		// else
		// {
		// rotation = gamma * directionOfRotation;
		// }
		// }
		// else
		// {
		// if (beta < gamma)
		// {
		// rotation = beta * directionOfRotation;
		// }
		// else
		// {
		// rotation = gamma * directionOfRotation;
		// }
		// }
		// }
		// }
		// }
		// }
		// return rotation;

		// //////////////////////////////////////////////////////
		// double first = 0;
		// double second = 0;
		// for (int i = 0; i < bestMatchingPoints.length - 1; i++)
		// {
		// first += Math.atan2(bestMatchingPoints[i][0].y - bestMatchingPoints[i
		// + 1][0].y, bestMatchingPoints[i][0].x - bestMatchingPoints[i +
		// 1][0].x);
		// second += Math.atan2(bestMatchingPoints[i][1].y -
		// bestMatchingPoints[i + 1][1].y, bestMatchingPoints[i][1].x -
		// bestMatchingPoints[i + 1][1].x);
		// }

		// return (second - first) / bestMatchingPoints.length;
		// //////////////////////////////////////////////////////
		// for (int i = 0; i < bestMatchingPoints.length; i++)
		// {
		// first += (bestMatchingPoints[i][1].x * bestMatchingPoints[i][0].y -
		// bestMatchingPoints[i][1].y * bestMatchingPoints[i][0].x);
		// second += (bestMatchingPoints[i][1].x * bestMatchingPoints[i][0].x +
		// bestMatchingPoints[i][1].y * bestMatchingPoints[i][0].y);
		// }
		// Rotation = Math.Atan(first / second);
		// Rotation = Math.atan2(bestMatchingPoints[0][1].y -
		// bestMatchingPoints[0][0].y, bestMatchingPoints[0][1].x -
		// bestMatchingPoints[0][0].x);

		if (Centroid == null) {
			return 0;
		}
		double[][] srcPoints = new double[bestMatchingPoints.length][];
		double[][] dstPoints = new double[bestMatchingPoints.length][];
		ArrayList<Matrix> srcMatrixWithCentriod = new ArrayList<Matrix>();
		ArrayList<Matrix> dstMatrixWithCentriod = new ArrayList<Matrix>();
		ArrayList<Matrix> holderMatrix = new ArrayList<Matrix>();
		for (int i = 0; i < bestMatchingPoints.length; i++) {
			srcPoints[i] = new double[2];
			dstPoints[i] = new double[2];
			srcPoints[i][0] = bestMatchingPoints[i][0].x;
			srcPoints[i][1] = bestMatchingPoints[i][0].y;
			dstPoints[i][0] = bestMatchingPoints[i][1].x;
			dstPoints[i][0] = bestMatchingPoints[i][1].y;
			srcPoints[i][0] = srcPoints[i][0] - Centroid[0].x;
			srcPoints[i][1] = srcPoints[i][1] - Centroid[0].y;
			dstPoints[i][0] = dstPoints[i][0] - Centroid[1].x;
			dstPoints[i][1] = dstPoints[i][1] - Centroid[1].y;
			srcMatrixWithCentriod.add(new Matrix(srcPoints[i], 1));
			dstMatrixWithCentriod.add(new Matrix(dstPoints[i], 1));
			holderMatrix
					.add(srcMatrixWithCentriod
							.get(i)
							.transpose()
							.times(dstMatrixWithCentriod.get(i).transpose()
									.transpose()));
		}
		Matrix H = new Matrix(2, 2, 0);
		for (int i = 0; i < bestMatchingPoints.length; i++) {
			H = H.plus(holderMatrix.get(i));
		}

		Matrix V = H.svd().getV();
		Matrix U = H.svd().getU();

		Matrix R = V.times(U.transpose());
		double[] rotation = R.getColumnPackedCopy();
		return Math.acos(rotation[0]);

	}

	private double calcMoreParameter(double a11, double a12, double a21,
			double a22) {
		ScalingX = Math.sqrt(Math.pow(a11, 2) + Math.pow(a12, 2));
		Scaling = Math.sqrt(Math.pow(a21, 2) + Math.pow(a22, 2));
		return (Math.atan2(a12, a11) + Math.atan2(a21, a22));

		// Skewing = Math.atan2(a11, a12) + Rotation;

	}

	public double getScalingX() {
		return 1;
	}

	public double getScaling() {
		return Scaling;
	}

	public double getSkewing() {
		return 0;
	}
}
