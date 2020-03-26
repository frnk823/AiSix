import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChessBoard extends JPanel {
    public static final int MARGIN=19;//边距
    public static final int SPAN=25;//网格间距
    public static final int ROWS=18;//棋盘行数
    public static final int COLS=18;//棋盘列数
    private Image img;

    Chess[] chessList=new Chess[(ROWS+1)*(COLS+1)];//初始每个数组元素为null
    int chessCount=0;//当前棋盘棋子的个数

    private boolean isGamming=false;    //是否正在游戏
    int computerColor;                    //计算机棋子颜色    1：黑棋  2：白棋
    boolean isComputerGo;         //计算机先行？
    boolean isBlack;                    //下一步棋是否该黑棋下子
    private Six f;

    int[][] boardStatus;

    int left ;
    int top ;
    int right ;
    int bottom ;

    public ChessBoard(Six f){
        this.f = f;
        boardStatus = new int[COLS+1][ROWS+1];
        for(int i=0; i<=COLS; i++){
            for(int j=0; j<=ROWS; j++){
                boardStatus[i][j] = 0;  //0空，1黑棋， 2白棋
            }
        }
        URL url=getClass().getClassLoader().getResource("board.jpg");
        img=Toolkit.getDefaultToolkit().getImage(url);
        this.addMouseListener(new MouseMonitor());
        this.addMouseMotionListener(new MouseMotionMonitor());
    }
    //画棋盘
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this);
        for(int i=0;i<=ROWS;i++){//画横线
            g.drawLine(MARGIN,  MARGIN + i*SPAN,MARGIN + COLS*SPAN,  MARGIN + i*SPAN);
        }
        for(int i=0;i<=COLS;i++){//画竖线
            g.drawLine(MARGIN + i*SPAN,  MARGIN,MARGIN + i*SPAN,  MARGIN + ROWS*SPAN);
        }
        g.fillRect(MARGIN + 3*SPAN - 2,        MARGIN + 3*SPAN - 2,        5, 5);
        g.fillRect(MARGIN + (COLS/2)*SPAN - 2, MARGIN + 3*SPAN - 2,        5, 5);
        g.fillRect(MARGIN + (COLS-3)*SPAN - 2, MARGIN + 3*SPAN - 2,        5, 5);

        g.fillRect(MARGIN + 3*SPAN - 2,        MARGIN + (ROWS/2)*SPAN - 2, 5, 5);
        g.fillRect(MARGIN+ (COLS/2)*SPAN- 2, MARGIN+ (ROWS/2)*SPAN -2, 5, 5);
        g.fillRect(MARGIN+ (COLS-3)*SPAN- 2, MARGIN+ (ROWS/2)*SPAN- 2, 5, 5);

        g.fillRect(MARGIN + 3*SPAN - 2,        MARGIN + (ROWS-3)* SPAN - 2, 5, 5);
        g.fillRect(MARGIN+ (COLS/2)*SPAN- 2, MARGIN+ (ROWS-3)*SPAN- 2, 5, 5);
        g.fillRect(MARGIN+ (COLS-3)*SPAN- 2, MARGIN+ (ROWS-3)*SPAN- 2, 5, 5);
        for(int i=0;i<chessCount;i++){
            chessList[i].draw(g);
            if(i==chessCount-1){//如果是最后一个棋子
                //网格交叉点x，y坐标
                int xPos=chessList[i].getCol()*SPAN+MARGIN;
                int yPos=chessList[i].getRow()*SPAN+MARGIN;
                g.setColor(Color.red);
                g.drawRect(xPos-Chess.DIAMETER/2, yPos-Chess.DIAMETER/2, Chess.DIAMETER, Chess.DIAMETER);
            }
        }
    }
    public Dimension getPreferredSize(){
        return new Dimension(MARGIN*2+SPAN*COLS, MARGIN*2 +SPAN*ROWS);
    }
    private boolean hasChess(int col,int row){
        for(int i = 0; i< chessCount; i++){
            Chess ch = chessList[i];
            if(ch!=null&&ch.getCol()==col&&ch.getRow()==row)
                return true;
        }
        return false;
    }
    private boolean hasChess(int col, int row, Color color){
        for(int i=0; i<chessCount; i++){
            Chess ch = chessList[i];
            if(ch!=null&&ch.getCol()==col&&ch.getRow()==row &&ch.getColor()==color)
                return true;
        }
        return false;
    }
    private boolean isWin(int col, int row){
        int continueCount=1;//连续棋子的个数
        Color c=isBlack?Color.black:Color.white;

        //横向向左寻找
        for(int x=col-1;x>=0;x--){
            if(hasChess(x,row,c)){
                continueCount++;
            }else
                break;
        }
        //横向向右寻找
        for(int x=col+1;x<=COLS;x++){
            if(hasChess(x,row,c)){
                continueCount++;
            }else
                break;
        }
        if(continueCount>=6){
            return true;
        }else
            continueCount=1;

        //继续另一种搜索纵向
        //向上搜索
        for(int y=row-1;y>=0;y--){
            if(hasChess(col,y,c)){
                continueCount++;
            }else
                break;
        }
        //纵向向下寻找
        for(int y=row+1;y<=ROWS;y++){
            if(hasChess(col,y,c))
                continueCount++;
            else
                break;

        }
        if(continueCount>=6)
            return true;
        else
            continueCount=1;


        //继续另一种情况的搜索：右上到左下
        //向右上寻找
        for(int x=col+1,y=row-1; y>=0&&x<=COLS; x++,y--){
            if(hasChess(x,y,c)){
                continueCount++;
            }
            else break;
        }
        //向左下寻找
        for(int x=col-1,y=row+1; x>=0&&y<=ROWS; x--,y++){
            if(hasChess(x,y,c)){
                continueCount++;
            }
            else break;
        }
        if(continueCount>=6)
            return true;
        else continueCount=1;


        //继续另一种情况的搜索：左上到右下
        //向左上寻找
        for(int x=col-1,y=row-1; x>=0&&y>=0; x--,y--){
            if(hasChess(x,y,c))
                continueCount++;
            else break;
        }
        //右下寻找
        for(int x=col+1,y=row+1; x<=COLS&&y<=ROWS; x++,y++){
            if(hasChess(x,y,c))
                continueCount++;
            else break;
        }
        if(continueCount>=6)
            return true;
        else
            return false;
    }

    class MouseMonitor extends MouseAdapter{
        public void mousePressed(MouseEvent e){//鼠标在组件上按下时调用
            if(!isGamming) return;
            if(isComputerGo) return;

            //将鼠标点击的坐标位置转换成网格索引
            int col=(e.getX()-MARGIN+SPAN/2)/SPAN;
            int row=(e.getY()-MARGIN+SPAN/2)/SPAN;

            //落在棋盘外不能下
            if(col<0||col>COLS||row<0||row>ROWS)
                return;

            //如果x，y位置已经有棋子存在，不能下
            if(hasChess(col, row))return;

            manGo(col,row);
            if(!isGamming)  return;

            if (isComputerGo&&isGamming){
                computerGo();
            }

        }
    }
    class MouseMotionMonitor extends MouseMotionAdapter{
        public void mouseMoved(MouseEvent e){
            int col=(e.getX()-MARGIN+SPAN/2)/SPAN;
            int row =(e.getY()-MARGIN+SPAN/2)/SPAN;
            if(col<0||col>ROWS||row<0||row>COLS||!isGamming||hasChess(col,row))
                ChessBoard.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            else
                ChessBoard.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }


    public void restartGame(){
        //清除棋子
        for(int i=0;i<chessList.length;i++){
            chessList[i]=null;
        }
        for(int i=0; i<=COLS; i++){
            for(int j=0; j<=ROWS; j++){
                boardStatus[i][j] = 0;
            }
        }
        left = 7;
        top = 7;
        right = 7;
        bottom = 7;
        //恢复游戏相关的变量值
        isBlack=true;          //是否该黑方下棋
        isGamming=true;     //是否正在游戏
        isComputerGo = f.computerFirst.isSelected();  // 选中复选框，计算机先行
        computerColor = isComputerGo?1:2;		      //如果计算机先行，则执黑棋
        chessCount =0; //当前棋盘棋子个数

        if(isComputerGo){
            computerGo();   // 如果计算机先行，计算机先下一子
        }
        f.refreshStatus();

        paintComponent(this.getGraphics());
    }

    private void computerGo () {


        //String msg= String.format("displayComputerGo执行后");
        //JOptionPane.showMessageDialog(ChessBoard.this, msg);

        Evaluate e = new Evaluate(this);
        int pos[] = e.getTheBestPosition();
        putChess(pos[0], pos[1], isBlack?Color.black:Color.white);
        if(isComputerGo&&isGamming)
            computerGo();
    }

    public void manGo(int col, int row) {
        putChess(col, row, isBlack?Color.black:Color.white);
        if(isComputerGo&&isGamming) {
            f.refreshStatus();
        }
    }

    public void putChess(int col, int row, Color color){
        Chess ch=new Chess(ChessBoard.this, col, row,color);
        chessList[chessCount++]=ch;
        boardStatus[col][row] = (color==Color.BLACK)? 1:2;
        paintComponent(this.getGraphics());

        if(left>col)  left=col;;
        if(top>row) top = row;
        if( right<col) right = col;
        if( bottom< row) bottom=row;

        //如果胜出则给出提示信息，不能继续下棋
        if(isWin(col, row)){
            f.displayGameover();
            String msg;
            if(!isComputerGo) {
                msg = String.format("好厉害，你赢了！（⊙o⊙）");
            }
            else{
                msg = String.format("呃，你输了〒_〒");
            }
            JOptionPane.showMessageDialog(ChessBoard.this, msg);
            isGamming=false;
        }
        else if(chessCount%2==1) {
            isBlack = !isBlack;
            isComputerGo = !isComputerGo;
        }

    }

    public void goback(){
        if(chessCount==0)
            return ;
        if(chessCount%2==0) {
            int i = chessList[chessCount - 1].getCol();
            int j = chessList[chessCount - 1].getRow();
            boardStatus[i][j] = 0;
            chessList[chessCount - 1] = null;
            chessCount--;
            paintComponent(this.getGraphics());
        }
        else {
            int i = chessList[chessCount-1].getCol();
            int j = chessList[chessCount-1].getRow();
            boardStatus[i][j] = 0;
            chessList[chessCount-1]=null;
            chessCount --;
            paintComponent(this.getGraphics());

            i = chessList[chessCount-1].getCol();
            j = chessList[chessCount-1].getRow();
            boardStatus[i][j] = 0;
            chessList[chessCount-1]=null;
            chessCount --;
            paintComponent(this.getGraphics());

            i = chessList[chessCount-1].getCol();
            j = chessList[chessCount-1].getRow();
            boardStatus[i][j] = 0;
            chessList[chessCount-1]=null;
            chessCount --;
            paintComponent(this.getGraphics());
        }
    }

}
