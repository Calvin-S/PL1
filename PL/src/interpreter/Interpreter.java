package interpreter;

import java.util.Random;

import Dijkstra.ShortestPath;
import ast.BExpr;
import ast.Mem;
import ast.Negative;
import ast.Node;
import ast.Number;
import model.Critter;
import model.Food;
import model.Rock;
import sun.management.Sensor;

public class Interpreter {

	public Interpreter() {

	}

	public int evaluateBExpr(BExpr b) {

		if (b.getOperator().equals("not")) {
			return !evaluateBinop(b.getChildren().get(1));

		} else {

		}

	}

	// evaluates a SINGLE line, not a whole program
	public int evaluateExpr(Node n) throws Exception {

		if (n instanceof BExpr) {

			BExpr b = (BExpr) n;
			return evaluateBExpr(b);

		} else if (n instanceof Number) {
			Number r = (Number) n;
			return r.getNum();
		} else if (n instanceof Mem) {
			int index = evaluateExpression(n.getChildren().get(0));
			if (index >= critter.getMemory().length) {
				return 0;
			}
			return critter.getMemory()[index];

		} else if (n instanceof Negative) {
			int num = evaluateExpression(n.getChildren().get(0));
			return -1 * num;
		} else if (n instanceof Sensor) {
			Sensor s = (Sensor) n;
			if (s.getOperator().equals("nearby")) {

				int index = Math.abs(evaluateExpression(s.getChildren().get(0)) % 6);

				int newDir = (critter.getCurDir() + index) % 6;
				int[] coord = critter.coordAhead(newDir, 1);
				Object o = world.getHex(coord[0], coord[1]);
				if (o == null) {
					return 0;
				} else if (o instanceof Critter) {
					Critter c = (Critter) o;
					return c.getAppearance(newDir);
				} else if (o instanceof Food) {
					return (((Food) o).getTotalEnergy() + 1) * -1;
				} else if (o instanceof Rock) {
					return Constants.ROCK_VALUE;
				}
			} else if (s.getOperator().equals("ahead")) {
				int index = evaluateExpression(s.getChildren().get(0));
				if (index < 0) {
					index = 0;
				}
				int[] coord = critter.coordAhead(critter.getCurDir(), index);
				Object o = world.getHex(coord[0], coord[1]);
				if (o == null) {
					return 0;
				} else if (o instanceof Critter) {
					Critter c = (Critter) o;
					return c.getAppearance(critter.getCurDir());
				} else if (o instanceof Food) {
					return (((Food) o).getTotalEnergy() + 1) * -1;
				} else if (o instanceof Rock) {
					return Constants.ROCK_VALUE;
				}
				// catch weird cases later (ex: index =100 and it goes off the baord

			} else if (s.getOperator().equals("random")) {
				int index = evaluateExpression(s.getChildren().get(0));
				if (index < 2) {
					return 0;
				}
				Random r = new Random();
				return r.nextInt(index);
			} else if (s.getOperator().equals("smell")) {
				ShortestPath sp = new ShortestPath(world, critter);
				return sp.findShortestPath();
			}
		}

		throw new Exception();

	}

}
