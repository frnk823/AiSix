import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;


public class Six extends JFrame {
    private JPanel toolbar;
    private JButton startButton,backButton,exitButton;
    private ChessBoard boardPanel;
    JCheckBox computerFirst;
    private JLabel statusbar;

    private JLabel name = new JLabel(("By 冯懿"));

    public Six(){
        super("六子棋人机对战");
        toolbar = new JPanel();
        startButton = new JButton("重新开始");
        backButton = new JButton("悔棋");
        exitButton = new JButton("退出");
        computerFirst = new JCheckBox("电脑先手");
        toolbar.add(startButton);
        toolbar.add(backButton);
        toolbar.add(exitButton);
        toolbar.add(computerFirst);
        this.add(toolbar, BorderLayout.NORTH);

        statusbar = new JLabel("请点击「重新开始」");
        statusbar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        this.add(statusbar,BorderLayout.SOUTH);

        this.add(name, BorderLayout.SOUTH);

        boardPanel=new ChessBoard(this);
        this.add(boardPanel, BorderLayout.CENTER);

        ActionMonitor monitor = new ActionMonitor();
        startButton.addActionListener(monitor);
        backButton.addActionListener(monitor);
        exitButton.addActionListener(monitor);

        this.setLocation(200,200);
        this.pack();
        this.setResizable(false);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        String msg=String.format("【游戏规则】\n第一次黑方下一颗子，之后黑白双方轮流每次各下两子\n\n★注意★\n首次开局请点击「重新开始」\n\n祝您游戏愉快！\\ ( >O< ) / \n\nAI课设");
        JOptionPane.showMessageDialog(Six.this, msg);
    }

    public static void main(String[] args) {
        new Six();
    }

    public void refreshStatus(){
        statusbar.setText("游戏进行中...");
    }

    public void displayGameover(){
        statusbar.setText("游戏结束");
    }

    class ActionMonitor implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==startButton){
                boardPanel.restartGame();

            }
            else if(e.getSource() == backButton){
                boardPanel.goback();
            }
            else if(e.getSource() == exitButton){
                System.exit(0);
            }
        }
    }

}
