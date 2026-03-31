package org.networks.subject5.individual5_2;

import org.networks.subject5.individual5_1.singleThread.SingleThreadDownloader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"File Name", "URL", "Progress", "Status"},
            0
    );
    private final JTable table = new JTable(tableModel);
    private final List<DownloadTask> tasks = new ArrayList<>();

    public MainFrame() {
        setTitle("Multi-thread Downloader");
        setSize(800, 500);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField urlField = new JTextField();
        JButton addBtn = new JButton("Add Download");
        inputPanel.add(new JLabel("URL: "), BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);
        inputPanel.add(addBtn, BorderLayout.EAST);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);


        addBtn.addActionListener(e -> {
            String url = urlField.getText().trim();
            if (!url.isEmpty()) {
                String fileName = SingleThreadDownloader.getFileName(url);

                DownloadModel model = new DownloadModel(url, fileName);
                int row = tableModel.getRowCount();
                tableModel.addRow(new Object[]{fileName, url, "0%", DownloadStatus.DOWNLOADING});

                DownloadTask task = new DownloadTask(model, row, tableModel);
                tasks.add(task);
                task.execute();

                urlField.setText("");
            }
        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton pauseBtn = new JButton("Pause");
        JButton resumeBtn = new JButton("Resume");

        controlPanel.add(pauseBtn);
        controlPanel.add(resumeBtn);

        add(controlPanel, BorderLayout.SOUTH);

        pauseBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                DownloadTask task = tasks.get(selectedRow);
                task.setPaused(true);
                task.getModel().setStatus(DownloadStatus.PAUSED);
                tableModel.setValueAt(DownloadStatus.PAUSED, selectedRow, 3);
            } else {
                JOptionPane.showMessageDialog(this, "Спочатку виберіть завантаження в таблиці!");
            }
        });

        resumeBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                DownloadTask task = tasks.get(selectedRow);
                task.setPaused(false);
                task.getModel().setStatus(DownloadStatus.DOWNLOADING);
                tableModel.setValueAt(DownloadStatus.DOWNLOADING, selectedRow, 3);
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}