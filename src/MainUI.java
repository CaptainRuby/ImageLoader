import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MainUI {
    private JTextField textFieldM;
    private JButton ButtonM;
    private JTextField textFieldH;
    private JTextField textFieldXH;
    private JButton ButtonH;
    private JButton ButtonXH;
    private JButton ButtonExcute;
    public JPanel panel;
    private JPanel dragPanel;
    private JLabel imageViewM;
    private JLabel imageViewH;
    private JLabel imageViewXH;
    private JTextField textFieldRename;
    private JTextField textFieldProject;
    private JButton ButtonProject;
    private List<File> files;
    private ArrayList<String> paths;
    private JFileChooser jFileChooser = new JFileChooser();

    public MainUI() {
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        imageViewM.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageViewM.setHorizontalAlignment(JLabel.CENTER);
        imageViewH.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageViewH.setHorizontalAlignment(JLabel.CENTER);
        imageViewXH.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imageViewXH.setHorizontalAlignment(JLabel.CENTER);
        textFieldProject.setText("具体到模块，如 C:\\WeCar-Navi\\TMapAutoMainApp");
        drag();//启用拖拽
        ButtonProject.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                jFileChooser.showOpenDialog(panel);
                if (jFileChooser.getSelectedFile() != null) {
                    String path = jFileChooser.getSelectedFile().getAbsolutePath();
                    textFieldProject.setText(path);
                    textFieldM.setText(path + "\\src\\main\\res\\drawable-mdpi");
                    textFieldH.setText(path + "\\src\\main\\res\\drawable-hdpi");
                    textFieldXH.setText(path + "\\src\\main\\res\\drawable-xhdpi");
                }
            }
        });
        ButtonM.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jFileChooser.setCurrentDirectory(new File(textFieldM.getText()));
                jFileChooser.showOpenDialog(panel);
                if (jFileChooser.getSelectedFile() != null) {
                    String path = jFileChooser.getSelectedFile().getAbsolutePath();
                    textFieldM.setText(path);
                }
            }
        });
        ButtonH.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jFileChooser.setCurrentDirectory(new File(textFieldH.getText()));
                jFileChooser.showOpenDialog(panel);
                if (jFileChooser.getSelectedFile() != null) {
                    String path = jFileChooser.getSelectedFile().getAbsolutePath();
                    textFieldH.setText(path);
                }
            }
        });
        ButtonXH.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jFileChooser.setCurrentDirectory(new File(textFieldXH.getText()));
                jFileChooser.showOpenDialog(panel);
                if (jFileChooser.getSelectedFile() != null) {
                    String path = jFileChooser.getSelectedFile().getAbsolutePath();
                    textFieldXH.setText(path);
                }
            }
        });
        ButtonExcute.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (imageViewM.getIcon() == null || imageViewH.getIcon() == null || imageViewXH.getIcon() == null) {
                    JOptionPane.showInternalMessageDialog(panel, "请导入图标", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (textFieldRename.getText() == null || textFieldRename.getText().length() <= 0) {
                    JOptionPane.showInternalMessageDialog(panel, "请输入文件重命名", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (textFieldM.getText() == null || textFieldM.getText().length() <= 0) {
                    JOptionPane.showInternalMessageDialog(panel, "请输入mdpi文件夹的地址", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (textFieldH.getText() == null || textFieldH.getText().length() <= 0) {
                    JOptionPane.showInternalMessageDialog(panel, "请输入hdpi文件夹的地址", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (textFieldXH.getText() == null || textFieldXH.getText().length() <= 0) {
                    JOptionPane.showInternalMessageDialog(panel, "请输入xhdpi文件夹的地址", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String rename = textFieldRename.getText();
                for (String path : paths) {
                    String target;
                    if (path.contains("@1.5")) {
                        target = textFieldXH.getText() + "\\" + rename + ".png";
                    } else if (path.contains("@1.25")) {
                        target = textFieldH.getText() + "\\" + rename + ".png";
                    } else {
                        target = textFieldM.getText() + "\\" + rename + ".png";
                    }
                    System.out.println(path);
                    System.out.println(target);
                    copy(new File(path), new File(target));
                }
                //重置
                imageViewM.setIcon(null);
                imageViewH.setIcon(null);
                imageViewXH.setIcon(null);
                textFieldRename.setText("");
                paths.clear();
                JOptionPane.showInternalMessageDialog(panel, "完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void drag()//定义的拖拽方法
    {
        //panel表示要接受拖拽的控件
        new DropTarget(panel, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dropEvent)//重写适配器的drop方法
            {
                try {
                    if (dropEvent.isDataFlavorSupported(DataFlavor.javaFileListFlavor))//如果拖入的文件格式受支持
                    {
                        dropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);//接收拖拽来的数据
                        files = (List<File>) (dropEvent.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                        if (files.size() > 3) {
                            JOptionPane.showInternalMessageDialog(panel, "最多只能拖拽3个", "提示", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        paths = new ArrayList<>(files.size());
                        for (File file : files) {
                            String path = file.getAbsolutePath();
                            paths.add(path);
                            ImageIcon imageIcon = new ImageIcon(path);
                            if (path.contains("@1.5")) {
                                imageIcon.setImage(imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                                imageViewXH.setIcon(imageIcon);
                            } else if (path.contains("@1.25")) {
                                imageIcon.setImage(imageIcon.getImage().getScaledInstance(83, 83, Image.SCALE_SMOOTH));
                                imageViewH.setIcon(imageIcon);
                            } else {
                                imageIcon.setImage(imageIcon.getImage().getScaledInstance(67, 67, Image.SCALE_SMOOTH));
                                imageViewM.setIcon(imageIcon);
                            }
                        }
                        dropEvent.dropComplete(true);//指示拖拽操作已完成
                    } else {
                        dropEvent.rejectDrop();//否则拒绝拖拽来的数据
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void copy(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException e) {
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
