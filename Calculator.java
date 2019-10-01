package calculator03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import calculator02.CalcException;

/**
 * Calculatorクラス
 * @author miyauchi
 *
 */
public class Calculator {
	/**
	 *
	 * 実行メソッド
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("計算式を入力");

		//値を入力する。
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String formula = br.readLine();

		//try-catchで例外をキャッチしてメッセージを投げる
		try {
			System.out.println(result(formula));
		} catch (CalcException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 計算結果を求め、返すresultメソッド
	 * @param formula 入力値
	 * @return String 計算結果
	 * @throws CalcException
	 */
	public static String result(String formula) throws CalcException {
		//結果変数
		String result;

		//入力値に"("が含まれる場合、含まれない場合
		if (formula.contains("(")) {
			//エラーチェック、例外処理
			checkErrorBracket(formula);

			//入力値を配列に格納
			String[] fomlArray = formula.split("");

			//リストを作成し、入力値を一文字ずつ格納
			List<String> resultFomlList = new ArrayList<>();
			for (int i = 0; i < fomlArray.length; i++) {
				resultFomlList.add(fomlArray[i]);
			}

			//"("と")"のインデックスを変数に代入
			int closeBracket = formula.indexOf(")");
			int startBracket = formula.lastIndexOf("(", closeBracket);

			//"("の前が -、の場合、-を-1*と置き換える
			if (startBracket > 0 && closeBracket > 0 && "-".equals(resultFomlList.get(startBracket - 1))) {
				resultFomlList.set(startBracket - 1, "-1*");
			}

			//()内の計算式を抜き出す
			String substFormula = formula.substring(startBracket + 1, closeBracket);

			//上記の処理で抜き出した()内の計算結果をresultに代入
			result = String.valueOf(parseAndCalc(substFormula));

			//resultを()内全体と置き換える
			resultFomlList.set(startBracket, result);
			resultFomlList.subList(startBracket + 1, closeBracket + 1).clear();

			//resultFomlListの中身を再度String型の変数に代入する
			StringBuilder sb = new StringBuilder();

			//StringBuilderにリストの中身を追加していくループ
			for (int i = 0; i < resultFomlList.size(); i++) {
				sb.append(resultFomlList.get(i));
			}

			//sbをString型に直して代入
			String kpi = String.valueOf(sb);

			//resultメソッドの再帰処理
			result = result(kpi);

		} else {
			result = String.valueOf(parseAndCalc(formula));
		}
		return result;
	}

	/**
	 * parseAndCalcメソッド
	 * Stringをintに変えて、計算して戻す。
	 * @param 入力された文字列としての計算式
	 */
	public static int parseAndCalc(String formula) throws CalcException {

		//判断基準となる数字のリスト
		List<Character> numbersList = new ArrayList<>();
		Collections.addAll(numbersList, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

		//文字列を1文字ずつ格納するリスト
		List<Character> chList = new ArrayList<>();

		//入力された文字列をchar型にしてリストに格納する
		for (int i = 0; i < formula.length(); i++) {
			chList.add(formula.charAt(i));
		}

		//判断基準となる演算子のリスト
		List<Character> opeList = new ArrayList<>();
		opeList.add('+');
		opeList.add('-');
		opeList.add('*');
		opeList.add('/');

		//数式を格納するリスト
		List<String> fomlList = new ArrayList<>();

		//数を項・演算子単位でまとめて、fomlListに格納するための変数
		StringBuilder num = new StringBuilder();

		//19のエラーを判断するためのリスト
		List<String> error19List = new ArrayList<>();
		Collections.addAll(error19List, "++", "+-", "--", "-+", "**", "*/", "//", "/*");

		//25のエラーを判断するためのリスト
		List<String> error25List = new ArrayList<>();
		Collections.addAll(error25List, "+*", "+/", "-*", "-/");

		//formulaにerror19Listの中身があった場合、例外をスロー
		for (int i = 0; i < error19List.size(); i++) {

			//入力値formulaにerror19Listの中身があった場合
			if (formula.contains(error19List.get(i))) {
				throw new CalcException("演算式が不正です。(19:加算減算、乗算割算連続)");
			}
		}

		//formulaにerror25Listの中身があった場合、例外をスロー
		for (int i = 0; i < error25List.size(); i++) {

			//入力値formulaにerror25Listの中身があった場合
			if (formula.contains(error25List.get(i))) {
				throw new CalcException("演算式が不正です。(25:加算減算の次に乗算割算)");
			}
		}

		//chListの中身をfomlListに格納
		for (int i = 0; i < chList.size(); i++) {
			char c = chList.get(i);

			//先頭に「*」「/」があった場合
			if ('*' == chList.get(0) || '/' == chList.get(0)) {
				throw new CalcException("演算式が不正です。(26:先頭乗算割算）");
			}

			//chListから取り出した中に演算子があった場合、数字の場合、その他の場合
			if (opeList.contains(c)) {

				//-, +, その他の場合
				if (c == '-') {
					if (num.length() != 0) {
						fomlList.add(num.toString());
					}
					num = new StringBuilder();
					num.append(chList.get(i).toString());
				} else if (c == '+') {
					if (num.length() != 0) {
						fomlList.add(num.toString());
					}
					num = new StringBuilder();
				} else {
					fomlList.add(num.toString());
					fomlList.add(chList.get(i).toString());
					num = new StringBuilder();
				}
			} else if (numbersList.contains(c)) {
				num.append(chList.get(i).toString());
			} else {
				throw new CalcException("演算式に不正な文字が含まれています。(21～23)");
			}

		}
		//最後の数字をfomlListに格納
		fomlList.add(String.valueOf(num));

		//計算
		//掛け算・割り算・足し算
		for (int i = 0; i < fomlList.size(); i++) {
			String fo = fomlList.get(i);

			//掛け算の場合
			if ("*".equals(fo)) {
				long multi = 0;

				try {
					int left = Integer.parseInt(fomlList.get(i - 1));
					int right = Integer.parseInt(fomlList.get(i + 1));

					multi = Math.multiplyExact(left, right);
					checkOverflow(multi);

				} catch (NumberFormatException e) {
					throw new CalcException("演算式が不正です。(24:最後の文字不正）");
				} catch (ArithmeticException e) {
					throw new CalcException("オーバーフローです。");
				}

				fomlList.set(i, String.valueOf(multi));

				fomlList.remove(i - 1);
				fomlList.remove(i);
				--i;
			}

			//割り算の場合
			if ("/".equals(fo)) {
				int divide = 0;

				try {
					int left = Integer.parseInt(fomlList.get(i - 1));
					int right = Integer.parseInt(fomlList.get(i + 1));

					divide = left / right;
				} catch (NumberFormatException e) {
					throw new CalcException("演算式が不正です。(24:最後の文字不正）");
				} catch (ArithmeticException e) {
					throw new CalcException("0除算です。(18:0除算）");
				}

				fomlList.set(i, String.valueOf(divide));

				fomlList.remove(i - 1);
				fomlList.remove(i);
				--i;

			}

		}

		//計算結果を代入する変数result
		int result = 0;

		//足し算
		for (int i = 0; i < fomlList.size(); i++) {
			try {
				long sum = Long.parseLong(fomlList.get(i));
				checkOverflow(sum);
				result = Math.addExact(result, (int) sum);

			} catch (NumberFormatException e) {
				throw new CalcException("演算式が不正です。(24:最後の文字不正）");
			} catch (ArithmeticException e) {
				throw new CalcException("オーバーフローです。");
			}
		}

		//計算結果を戻す
		return result;

	}

	/**
	 * チェックオーバーフローメソッド
	 * @param lo 足し算や掛け算の結果
	 * @throws CalcException
	 */
	public static void checkOverflow(Long lo) throws CalcException {

		//intの閾値を越えた場合
		if (lo > Integer.MAX_VALUE || lo < Integer.MIN_VALUE) {
			throw new CalcException("オーバーフローです。");
		}
	}

	/**
	 * チェックエラーブラケットメソッド ()処理に関わる例外を投げる
	 * @param formula 入力値
	 * @throws CalcException
	 */
	public static void checkErrorBracket(String formula) throws CalcException {

		//入力値を一文字ずつリストに格納する
		List<Character> chList = new ArrayList<>();
		for (int i = 0; i < formula.length(); i++) {
			chList.add(formula.charAt(i));

		}

		//0-9までの数字をチェックするためのリスト
		List<Character> numbersList = new ArrayList<>();
		Collections.addAll(numbersList, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

		//演算子をチェックするためのリスト
		List<Character> opeList = new ArrayList<>();
		opeList.add('+');
		opeList.add('-');
		opeList.add('*');
		opeList.add('/');

		//括弧が連続する場合
		if (formula.contains(")(")) {
			throw new CalcException("演算式が不正です。（37:括弧連続）");
		}

		//括弧の数をカウントし、"("と")"の数が合うか確認
		int count = 0;
		for (char x : chList) {
			if ('(' == x) {
				++count;
			} else if (')' == x) {
				--count;
			}
		}
		//括弧の数が合わない場合
		if (count != 0) {
			throw new CalcException("括弧数が不正です。(36:括弧数不正）");
		}

		//()のインデックスを取得
		int closeBracket = formula.indexOf(")");
		int startBracket = formula.lastIndexOf("(", closeBracket);

		//括弧と数値が連続する場合
		if (startBracket > 0) {
			if (numbersList.contains(chList.get(startBracket - 1))) {
				throw new CalcException("演算式が不正です。（38:括弧数値連続）");
			}
		} else if ((closeBracket + 1) < chList.size()) {
			if (numbersList.contains(chList.get(closeBracket + 1))) {
				throw new CalcException("演算式が不正です。（38:数値括弧連続）");
			}
		}

	}

}
