package calculator03;

/**
 * CalcExceptionクラス
 * @author miyauchi
 *
 */
public class CalcException extends Exception {
	/**
	 * コンストラクタ
	 * @param message 例外時に投げるメッセージ
	 */
	public CalcException(String message) {
		super(message);
	}
}
