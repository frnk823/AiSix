
//@author 冯懿

public class Evaluate {
	private static final int SIX  =          500000;
	private static final int HUO_FIVE  =     50000;
	private static final int MIAN_FIVE  =    10000;
	private static final int HUO_FOUR  =     5000;
	private static final int MIAN_FOUR =     1000;
	private static final int HUO_THREE =     500;
	private static final int MENGLONG_THREE =300;
	private static final int MIAN_THREE =    100;
	private static final int HUO_TWO =       100;
	private static final int MIAN_TWO =      50;

	private static final int LARGE_NUMBER = 10000000;
	private static int SEARCH_DEPTH = 5;
	private static int SAMPLE_NUMBER = 10;



	private ChessBoard cb;
	private int[][] blackValue;   // 保存每一空位下黑子的价值
	private int[][] whiteValue;   // 保存每一空位下白子的价值
	private int[][] staticValue ;  // 保存每一点的位置价值，越靠中心，价值越大

	public Evaluate(ChessBoard cb) {
		this.cb = cb;

		blackValue = new int[ChessBoard.COLS+1][ChessBoard.ROWS+1];
		whiteValue = new int[ChessBoard.COLS+1][ChessBoard.ROWS+1];
		staticValue = new int[ChessBoard.COLS+1][ChessBoard.ROWS+1];

		for(int i=0; i<=ChessBoard.COLS; i++){
			for(int j=0; j<=ChessBoard.ROWS; j++){
				blackValue[i][j] = 0;
				whiteValue[i][j] = 0;
			}
		}
		for(int i=0; i<=ChessBoard.COLS/2; i++){
			for(int j=0; j<=ChessBoard.ROWS/2; j++){
				staticValue[i][j] = i<j? i:j;
				staticValue[ChessBoard.COLS-i][j] = staticValue[i][j];
				staticValue[i][ChessBoard.ROWS-j] = staticValue[i][j];
				staticValue[ChessBoard.COLS-i][ChessBoard.ROWS-j] = staticValue[i][j];
			}
		}
	}


	private void getTheSpaceValues(){
		int l, t, r, b;
		l = (cb.left >2) ? cb.left-2:0;
		t = (cb.top >2) ? cb.top-2:0;
		r = (cb.right < cb.COLS-1)? cb.right+2 : cb.COLS;
		b = (cb.bottom < cb.ROWS-1)? cb.bottom+2 : cb.ROWS;
		for(int i=l; i<=r; i++){
			for(int j=t; j<=b; j++){//对棋盘的所有点循环
				blackValue[i][j] = 0;
				whiteValue[i][j] = 0;
				if(cb.boardStatus[i][j]==0){  //如果是空位，进行估值
					for(int m=1; m<=4; m++){ //每个点的分值为四个方向分值之和
						blackValue[i][j] += evaluateValue( 1, i, j, m );
						whiteValue[i][j] += evaluateValue( 2, i, j, m );
					}
				}
			}
		}
	}
	/**
	 * 获取计算机的最佳下棋位置
	 * @return：最佳位置的坐标
	 */
	int[] getTheBestPosition(){
		getTheSpaceValues();
		int maxValue = -LARGE_NUMBER;
		int value;
		int[] position = new int[2];

		int valuablePositions[][] = getTheMostValuablePositions();

		for(int i=0; i<valuablePositions.length; i++){
			if(valuablePositions[i][2]>=SIX){   //已经连六
				position[0] = valuablePositions[i][0];
				position[1] = valuablePositions[i][1];
				break;
			}
			cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = cb.computerColor;
			int oldLeft= cb.left; //改变LEFT、TOP等值
			int oldTop= cb.top; //改变LEFT、TOP等值
			int oldRight= cb.right; //改变LEFT、TOP等值
			int oldBottom = cb.bottom; //改变LEFT、TOP等值
			if(cb.left>valuablePositions[i][0])  cb.left=valuablePositions[i][0];
			if(cb.top>valuablePositions[i][1])  cb.top = valuablePositions[i][1];
			if( cb.right<valuablePositions[i][0]) cb.right = valuablePositions[i][0];
			if( cb.bottom< valuablePositions[i][1]) cb.bottom=valuablePositions[i][1];

			value = min(SEARCH_DEPTH, -LARGE_NUMBER, LARGE_NUMBER);

			cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = 0;
			cb.left=oldLeft;
			cb.top = oldTop;
			cb.right= oldRight;
			cb.bottom=oldBottom;

			if( value > maxValue){
				maxValue = value;
				position[0] = valuablePositions[i][0];
				position[1] = valuablePositions[i][1];
			}
		}
		return  position;
	}
	/**
	 * @param depth：搜索的深度
	 * @return
	 */
	private int min(int depth,int alpha, int beta) {
		if(depth == 0){ //如果搜索到最底层，直接返回当前的估值。
			return evaluateGame();
		}
		getTheSpaceValues();

		int value;
		int valuablePositions[][] = getTheMostValuablePositions();

		for(int i=0; i<valuablePositions.length; i++){
			cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = cb.computerColor==1? 2:1;
			int oldLeft= cb.left;
			int oldTop= cb.top;
			int oldRight= cb.right;
			int oldBottom = cb.bottom;
			if(cb.left>valuablePositions[i][0])     cb.left=valuablePositions[i][0];
			if(cb.top>valuablePositions[i][1])      cb.top = valuablePositions[i][1];
			if( cb.right<valuablePositions[i][0])     cb.right = valuablePositions[i][0];
			if( cb.bottom< valuablePositions[i][1]) cb.bottom=valuablePositions[i][1];

			value = max(depth-1, alpha,  beta);

			cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = 0;
			cb.left=oldLeft;
			cb.top = oldTop;
			cb.right= oldRight;
			cb.bottom=oldBottom;

			if(value < beta){
				beta = value;
				if(alpha >= beta){
					return alpha;
				}
			}
		}
		return beta;
	}

	/**
	 * @param depth：搜索的深度
	 * @return
	 */
	private int max(int depth, int alpha, int beta) {
		if(depth == 0){ //如果搜索到最底层，直接返回当前的估值。
			return evaluateGame();
		}
		getTheSpaceValues();

		int value;
		int valuablePositions[][] = getTheMostValuablePositions();

		for(int i=0; i<valuablePositions.length; i++){
			cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = cb.computerColor;
			int oldLeft= cb.left;
			int oldTop= cb.top;
			int oldRight= cb.right;
			int oldBottom = cb.bottom;
			if(cb.left>valuablePositions[i][0])  cb.left=valuablePositions[i][0];
			if(cb.top>valuablePositions[i][1])  cb.top = valuablePositions[i][1];
			if( cb.right<valuablePositions[i][0]) cb.right = valuablePositions[i][0];
			if( cb.bottom< valuablePositions[i][1]) cb.bottom=valuablePositions[i][1];

			value = min(depth-1,  alpha,  beta);

			cb.boardStatus[valuablePositions[i][0]][valuablePositions[i][1]] = 0;
			cb.left=oldLeft;
			cb.top = oldTop;
			cb.right= oldRight;
			cb.bottom=oldBottom;

			if( value > alpha) {
				alpha = value;
				if(alpha >=beta){
					return beta;
				}
			}
		}
		return alpha;
	}

	/**
	 * 对数组按第三列（allValue[][2]降序排序)
	 * @param allValue: 待排序的数组，二维数组的前两列是棋盘位置坐标，第3列是该位置的价值
	 */
	private void sort(int [][] allValue) {
		for (int i = 0; i < allValue.length ; i++) {
			for (int j= 0; j < allValue.length - 1; j++) {
				int ti, tj, tvalue;
				if (allValue[j][2] <allValue[j + 1][2])  {
					tvalue = allValue[j][2];
					allValue[j] [2]= allValue[j + 1][2];
					allValue[j+1][2] =tvalue;

					ti = allValue[j][0];
					allValue[j][0] = allValue[j + 1][0];
					allValue[j+1][0] =ti;

					tj = allValue[j][1];
					allValue[j][1] = allValue[j + 1][1];
					allValue[j+1][1] =tj;

				}
			}
		}
	}
	/**
	 * 计算棋盘上指定空位在指定方向价值
	 * @param color   要计算的是哪一方的价值，1：黑方，2：白方
	 * @param col   要计算位置的列坐标
	 * @param row   要计算位置的行坐标
	 * @param dir   要计算方向，1：水平，2：垂直，3：左上到右下，4：右上到左下
	 * @return  价值
	 * @author 冯懿
	 * @Time2017-12-15
	 */
	private int evaluateValue( int color, int col, int row, int dir ) {
		int k, m;
		int value = 0;
		int chessCount1 = 1;  // 指定颜色的棋子数
		int chessCount2 = 0;  // 指定颜色的棋子数
		int chessCount3 = 0;  // 指定颜色的棋子数
		int spaceCount1= 0;  //一端的空位数
		int spaceCount2 = 0; //另一端空位数
		int spaceCount3 = 0; //另一端空位数
		int spaceCount4 = 0; //另一端空位数
		switch(dir){
			case 1: //水平方向
				//向增加的方向查找相同颜色连续的棋子
				for(k=col+1; k<=cb.COLS; k++ ){
					if(cb.boardStatus[k][row]==color){
						chessCount1++;
					}
					else{
						break;
					}
				}
				//在棋子尽头查找连续的空格数
				while((k<=cb.COLS) && (cb.boardStatus[k][row]==0)){
					spaceCount1++;
					k++;
				}
				if(spaceCount1==1){
					while((k<=cb.COLS)  && (cb.boardStatus[k][row]==color)){
						chessCount2++;
						k++;
					}
					while((k<=cb.COLS)  && (cb.boardStatus[k][row]==0)){
						spaceCount2++;
						k++;
					}
				}

				//向相反方向查找相同颜色连续的棋子
				for(k=col-1; k>=0; k-- ){
					if(cb.boardStatus[k][row]==color){
						chessCount1++;
					}
					else{
						break;
					}
				}
				//在棋子的尽头查找连续的空格数
				while(k>=0 && (cb.boardStatus[k][row]==0)){
					spaceCount3++;
					k--;
				}
				if(spaceCount3==1){
					while((k>=0)  && (cb.boardStatus[k][row]==color)){
						chessCount3++;
						k--;
					}
					while((k>=0) && (cb.boardStatus[k][row]==0)){
						spaceCount4++;
						k--;
					}
				}
				break;
			case 2:  //  垂直方向
				//向增加的方向查找相同颜色连续的棋子
				for(k=row+1; k<=cb.ROWS; k++ ){
					if(cb.boardStatus[col][k]==color){
						chessCount1++;
					}
					else{
						break;
					}
				}
				//在棋子尽头查找连续的空格数
				while((k<=cb.ROWS) && (cb.boardStatus[col][k]==0)){
					spaceCount1++;
					k++;
				}
				if(spaceCount1==1){
					while((k<=cb.ROWS)  && (cb.boardStatus[col][k]==color)){
						chessCount2++;
						k++;
					}
					while((k<=cb.ROWS)  && (cb.boardStatus[col][k]==0)){
						spaceCount2++;
						k++;
					}
				}

				//向相反方向查找相同颜色连续的棋子
				for(k=row-1; k>=0; k-- ){
					if(cb.boardStatus[col][k]==color){
						chessCount1++;
					}
					else{
						break;
					}
				}
				//在相反方向的棋子尽头查找连续的空格数
				while(k>=0 && (cb.boardStatus[col][k]==0)){
					spaceCount3++;
					k--;
				}
				if(spaceCount3==1){
					while((k>=0)  && (cb.boardStatus[col][k]==color)){
						chessCount3++;
						k--;
					}
					while((k>=0) && (cb.boardStatus[col][k]==0)){
						spaceCount4++;
						k--;
					}
				}
				break;
			case 3:  //  左上到右下
				//向增加的方向查找相同颜色连续的棋子
				for(k=col+1, m=row+1; (k<=cb.COLS) &&(m<=cb.ROWS); k++ ,m++){
					if(cb.boardStatus[k][m]==color){
						chessCount1++;
					}
					else{
						break;
					}
				}
				//在棋子尽头查找连续的空格数
				while((k<=cb.COLS) &&(m<=cb.ROWS)&& (cb.boardStatus[k][m]==0)){
					spaceCount1++;
					k++;
					m++;
				}
				if(spaceCount1==1){
					while((k<=cb.COLS)  &&(m<=cb.ROWS) && (cb.boardStatus[k][m]==color)){
						chessCount2++;
						k++;
						m++;
					}
					while((k<=cb.COLS) &&(m<=cb.ROWS)  && (cb.boardStatus[k][m]==0)){
						spaceCount2++;
						k++;
						m++;
					}
				}

				//向相反方向查找相同颜色连续的棋子
				for(k=col-1, m=row-1; (k>=0)&&(m>=0); k--,m-- ){
					if(cb.boardStatus[k][m]==color){
						chessCount1++;
					}
					else{
						break;
					}
				}
				//在相反方向的棋子尽头查找连续的空格数
				while( (k>=0) && (m>=0) && (cb.boardStatus[k][m]==0)){
					spaceCount3++;
					k--;
					m--;
				}
				if(spaceCount3==1){
					while((k>=0) && (m>=0) && (cb.boardStatus[k][m]==color)){
						chessCount3++;
						k--;
						m--;
					}
					while((k>=0)  && (m>=0) && (cb.boardStatus[k][m]==0)){
						spaceCount4++;
						k--;
						m--;
					}
				}
				break;

			case 4:  //  右上到左下
				for(k=col+1, m=row-1; k<=cb.COLS && m>=0; k++,m--){  //查找连续的同色棋子
					if(cb.boardStatus[k][m]==color){
						chessCount1++;
					}
					else{
						break;
					}
				}
				while(k<=cb.COLS && m>=0 && (cb.boardStatus[k][m]==0)){ //统计空位数
					spaceCount1++;
					k++;
					m--;
				}
				if(spaceCount1==1){
					while((k<=cb.COLS) &&( m>=0) && (cb.boardStatus[k][m]==color)){
						chessCount2++;
						k++;
						m--;
					}
					while((k<=cb.COLS) &&( m>=0) && (cb.boardStatus[k][m]==0)){
						spaceCount2++;
						k++;
						m--;
					}
				}

				for(k=col-1, m=row+1; k>=0 && m<=cb.ROWS; k--,m++){  //查找连续的同色棋子
					if(cb.boardStatus[k][m]==color){
						chessCount1++;
					}
					else{
						break;
					}
				}
				while(k>=0 && m<=cb.ROWS &&( cb.boardStatus[k][m]==0)){ // 统计空位数
					spaceCount3++;
					k--;
					m++;
				}
				if(spaceCount3==1){
					while((k>=0) &&( m<=cb.ROWS) && (cb.boardStatus[k][m]==color)){
						chessCount3++;
						k--;
						m++;
					}
					while((k>=0) &&( m<=cb.ROWS)  && (cb.boardStatus[k][m]==0)){
						spaceCount4++;
						k--;
						m++;
					}
				}
				break;
		}
		if(chessCount1 + chessCount2 +chessCount3 +spaceCount1 + spaceCount2  +spaceCount3 + spaceCount4>=6){ //只有同色棋子数加两端的空位数不少于6时，才有价值
			value = getValue(chessCount1,chessCount2, chessCount3,  spaceCount1, spaceCount2, spaceCount3, spaceCount4,color);
		}
		return value;
	}
	/**
	 * 根据棋型，计算该点下一个棋子的价值
	 * @param chessCount1    该空位置下一个棋子后同种颜色棋子连续的个数
	 * @param spaceCount1  连续棋子一端的连续空位数
	 * @param chessCount2  如果spaceCount1为1，继续连续同种颜色棋子的个数
	 * @param spaceCount2  继chessCount2之后，连续空位数
	 * @param spaceCount3  连续棋子另一端的连续空位数
	 * @param chessCount3  如果spaceCount3为1，继续连续同种颜色棋子的个数
	 * @param spaceCount4  继chessCount3之后，连续空位数

	 * @param color   棋子的颜色 1：黑子，2：白子
	 * @return  该点放color棋子给color方带来的价值
	 * @author 冯懿
	 * @Time2017-12-15
	 */
	private int getValue(int chessCount1,int chessCount2, int chessCount3,  int spaceCount1, int spaceCount2, int spaceCount3, int spaceCount4, int color) {
		int value = 0;
		//将六子棋棋型分为连六、活五、眠五、活四、眠四、活三、朦胧三、眠三、活二、眠二
		switch(chessCount1){
			case 6:  //如果已经可以连成6子，则赢棋
				value = SIX;
				break;

			case 5:if( (spaceCount1>0) && (spaceCount3>0) ){ //活五
				value = HUO_FIVE;
			}
			else if( ((spaceCount1==0) &&(spaceCount3>0)) ||((spaceCount1>0) &&(spaceCount3==0)) ){  //眠五
				value = MIAN_FIVE;
			}
				break;

			case 4:
				if( (spaceCount1>1) && (spaceCount3>1) ){ //活四
					value = HUO_FOUR;
				}
				else if(( (spaceCount1>1) && (spaceCount3==0) ) || ( (spaceCount1==0) && (spaceCount3>1) )){  //眠四
					value = MIAN_FOUR;
				}
				break;
			case 3:
				if( (spaceCount1>2) && (spaceCount3>2) ){ //OOOAAAOOO
					value = HUO_THREE;
				}
				else if( ( (spaceCount1==0) && (spaceCount3>3) ) || ( (spaceCount3>3) && (chessCount3==0) ) ){ // AAAOOO
					value = MIAN_THREE;
				}
				break;
			case 2:
				if( (spaceCount1>3) && (spaceCount3>3) ){ //活二
					value = HUO_TWO;
				}
				else if(( (spaceCount1>3) && (spaceCount3==0) ) || ( (spaceCount1==0) && (spaceCount3>3) )){  //眠二
					value = MIAN_TWO;
				}
				else if(((spaceCount1==1)&&(chessCount2==1)&&(spaceCount2==2)&&(spaceCount3==1))||((spaceCount1==1)&&(chessCount3==1)&&(spaceCount3==1)&&(spaceCount4==2))){//BOOAOAAOB
					value = MENGLONG_THREE;
				}
				break;
			case 1:
				if( ( (spaceCount1==2) &&(spaceCount3==1)&&(chessCount3==2)&&(spaceCount4==1) ) ||  ( (spaceCount1==1) &&(spaceCount2==1)&&(chessCount2==2)&&(spaceCount3==2) ) ){ // BOOAOAAOB
					value = MENGLONG_THREE;
				}
				break;
			default:
				value = 0;
				break;
		}
		return value;
	}
	/**
	 * 查找棋盘上价值最大的几个空位，每个空位的价值等于两种棋的价值之和。
	 * @return   价值最大的几个空位（包括位置和估值）
	 * @author 冯懿
	 * @Time2017-12-15
	 */

	private int[][] getTheMostValuablePositions(){
		int i,j,k=0;
		//allValue：保存每一空位的价值(列坐标，行坐标，价值）
		int [][] allValue = new int[(cb.COLS+1)*(cb.ROWS+1)][3];
		for(i=0;i<cb.COLS; i++){
			for(j=0;j<cb.ROWS; j++){
				if(cb.boardStatus[i][j]==0){
					allValue[k][0] = i;
					allValue[k][1] = j;
					allValue[k][2] = blackValue[i][j] + whiteValue[i][j]+staticValue[i][j];
					k++;
				}
			}
		}
		sort(allValue);   //按价值降序排序

		int size = k<SAMPLE_NUMBER? k:SAMPLE_NUMBER;
		int valuablePositions[][] = new int[size][3];

		//将allValue中的前size个空位赋给bestPositions
		for(i=0;i<size;i++){
			valuablePositions[i][0]=allValue[i][0];
			valuablePositions[i][1]=allValue[i][1];
			valuablePositions[i][2]=allValue[i][2];
		}
		return valuablePositions;
	}

	private int evaluateGame(){
		int value=0;
		int i,j,k;
		int[] line = new int[cb.COLS+1];
		//水平  对每一行估值
		for(j=0; j<=cb.ROWS; j++){
			for(i=0; i<=cb.COLS; i++){
				line[i] = cb.boardStatus[i][j];   //第一个下标是列下标
			}
			value += evaluateLine(line, cb.COLS+1, 1);
			value -= evaluateLine(line, cb.COLS+1, 2);
		}
		// 对每一列估值
		for(i=0; i<=cb.COLS; i++){
			for(j=0; j<=cb.ROWS; j++){
				line[j] = cb.boardStatus[i][j];
			}
			value += evaluateLine(line, cb.ROWS+1, 1);
			value -= evaluateLine(line, cb.ROWS+1, 2);
		}

		// 左下到右上斜线估值
		for(j = 4; j<=cb.ROWS; j++){
			for(k=0; k<=j;  k++){
				line[k] = cb.boardStatus[k][j-k];
			}
			value += evaluateLine(line, j+1, 1);
			value -= evaluateLine(line, j+1, 2);
		}
		for(j = 1; j<=cb.ROWS-4; j++){
			for(k=0; k<=cb.COLS-j; k++){
				line[k] = cb.boardStatus[k+j][cb.ROWS-k];
			}
			value += evaluateLine(line, cb.ROWS+1-j, 1);
			value -= evaluateLine(line, cb.ROWS+1-j, 2);
		}
		// 左上到右下斜线估值
		for(j = 0; j<=cb.ROWS-4; j++){
			for(k=0;k<=cb.ROWS-j; k++){
				line[k] = cb.boardStatus[k][k+j];
			}
			value += evaluateLine(line,cb.ROWS+1-j, 1);
			value -= evaluateLine(line, cb.ROWS+1-j, 2);
		}
		for(i= 1; i<=cb.COLS-4; i++){
			for(k=0;k<=cb.ROWS-i;k++){
				line[k] = cb.boardStatus[k+i][k];
			}
			value += evaluateLine(line,cb.ROWS+1-i, 1);
			value -= evaluateLine(line, cb.ROWS+1-i, 2);
		}
		if(cb.computerColor==1){
			return value;
		}
		else{
			return -value;
		}
	}

	private int evaluateLine(int lineState[], int num, int color)	{
		int chess, space1, space2;
		int i,j,k;
		int value = 0;
		int begin,end;
		for(i=0; i<num; i++)
			if(lineState[i] == color){  //遇到要找的棋子，检查棋型，得到对应的分值
				chess = 1;
				begin = i;
				for(j=begin+1; (j<num)&&(lineState[j]==color) ;j++  ){
					chess++;
				}
				if(chess<2){
					continue;
				}
				end = j-1;

				space1 = 0;
				space2=0;
				for(j=begin-1; (j>=0)&&((lineState[j]==0)||(lineState[j]==color)) ;j--  ){//棋子前面的空格
					space1++;
				}
				for(j=end+1; (j<num)&&((lineState[j]==0)||(lineState[j]==color)) ;j++  ){//棋子前面的空格
					space2++;
				}

				if(chess+space1+space2 >=6){
					value += getValue(chess, space1, space2);
				}
				i=end+1;
			}
		return value;
	}

	/**
	 * @param chess
	 * @param space1
	 * @param space2
	 * @return
	 */
	private int getValue(int chessCount, int spaceCount1, int spaceCount2) {
		int value = 0;
		//将六子棋棋型分为连六、活五、眠五、活四、眠四、活三、朦胧三、眠三、活二、眠二
		switch(chessCount){
			case 6://如果已经可以连成6子，则赢棋
				value = SIX;
				break;
			case 5:
				if( (spaceCount1>0) && (spaceCount2>0) ){ //活五
					value = HUO_FIVE;
				}
				break;
			case 4:
				if( (spaceCount1>0) && (spaceCount2>0) ){ //活四
					value = HUO_FOUR;
				}
				break;
			case 3:
				if( (spaceCount1>0) && (spaceCount2>0) ){//活三
					value = HUO_THREE;
				}
				break;
			case 2:
				if( (spaceCount1>0) && (spaceCount2>0) ){ //活二
					value = HUO_TWO;
				}
				break;
			default:
				value = 0;
				break;
		}
		return value;
	}


}

