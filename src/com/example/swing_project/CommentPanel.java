package com.example.swing_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class CommentPanel extends JPanel {

    private DefaultListModel<String> commentListModel;
    private Map<String, DefaultListModel<String>> replyMap; // 댓글에 대한 대댓글 리스트를 관리하는 맵
    private Map<String, JList<String>> visibleReplyLists; // 현재 보여지는 대댓글 리스트를 저장

    public CommentPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 240, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 댓글 레이블
        JLabel commentLabel = new JLabel("댓글");
        commentLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        commentLabel.setForeground(new Color(153, 102, 255));
        add(commentLabel, BorderLayout.NORTH);

        // 댓글 리스트
        commentListModel = new DefaultListModel<>();
        JList<String> commentList = new JList<>(commentListModel);
        commentList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        commentList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane commentScrollPane = new JScrollPane(commentList);
        add(commentScrollPane, BorderLayout.CENTER);

        // 대댓글 맵 초기화
        replyMap = new HashMap<>();
        visibleReplyLists = new HashMap<>(); // 보여지는 대댓글 리스트를 관리

        // 댓글 클릭 이벤트 처리 (대댓글 보이기/숨기기)
        commentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // 클릭 시
                    String selectedComment = commentList.getSelectedValue();
                    toggleReplyVisibility(selectedComment, commentList); // 대댓글 토글
                }
            }
        });

        // 댓글 입력 필드와 버튼
        JTextField commentField = new JTextField(30);
        commentField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JButton commentButton = new JButton("댓글 달기");
        commentButton.setBackground(new Color(204, 153, 255));
        commentButton.setForeground(Color.WHITE);
        commentButton.setFocusPainted(false);

        commentButton.addActionListener(e -> {
            String commentText = commentField.getText();
            if (!commentText.isEmpty()) {
                commentListModel.addElement(commentText); // 댓글 추가
                commentField.setText(""); // 입력 필드 초기화
                replyMap.put(commentText, new DefaultListModel<>()); // 대댓글 리스트 초기화
            }
        });

        JPanel commentInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commentInputPanel.setBackground(new Color(255, 240, 245));
        commentInputPanel.add(commentField);
        commentInputPanel.add(commentButton);
        add(commentInputPanel, BorderLayout.SOUTH);
    }

    // 대댓글 토글 (보이기/숨기기)
    private void toggleReplyVisibility(String comment, JList<String> commentList) {
        DefaultListModel<String> replies = replyMap.get(comment);
        if (replies == null || replies.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "대댓글이 없습니다.");
            return;
        }

        if (visibleReplyLists.containsKey(comment)) {
            // 대댓글이 이미 보이고 있다면 숨기기
            JList<String> replyList = visibleReplyLists.remove(comment);
            remove(replyList);
        } else {
            // 대댓글이 보이지 않으면 새로 추가
            JList<String> replyList = new JList<>(replies);
            replyList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            replyList.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JScrollPane replyScrollPane = new JScrollPane(replyList);
            replyScrollPane.setPreferredSize(new Dimension(200, 100));

            // 댓글 밑에 대댓글 리스트 추가
            add(replyScrollPane, BorderLayout.SOUTH);
            visibleReplyLists.put(comment, replyList);
        }

        // 레이아웃 업데이트
        revalidate();
        repaint();
    }

    // 대댓글 입력 창을 표시하는 메서드
    private void showReplyDialog(String comment) {
        if (comment == null || comment.isEmpty()) return;

        JDialog replyDialog = new JDialog((Frame) null, "답글 달기", true);
        replyDialog.setSize(400, 200);
        replyDialog.setLayout(new BorderLayout());
        replyDialog.setLocationRelativeTo(this);

        // 대댓글 입력 필드
        JTextField replyField = new JTextField(30);
        JPanel replyPanel = new JPanel(new BorderLayout());
        replyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        replyPanel.add(new JLabel("대댓글 입력: "), BorderLayout.NORTH);
        replyPanel.add(replyField, BorderLayout.CENTER);
        replyDialog.add(replyPanel, BorderLayout.CENTER);

        // 대댓글 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("등록");
        JButton cancelButton = new JButton("취소");

        // 대댓글 등록 버튼 클릭 시
        submitButton.addActionListener(e -> {
            String replyText = replyField.getText();
            if (!replyText.isEmpty()) {
                DefaultListModel<String> replies = replyMap.get(comment);
                if (replies == null) {
                    replies = new DefaultListModel<>();
                    replyMap.put(comment, replies); // 대댓글 리스트가 없을 경우 초기화
                }
                replies.addElement(replyText); // 대댓글 추가
                JOptionPane.showMessageDialog(this, "대댓글이 등록되었습니다.");
                replyDialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> replyDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        replyDialog.add(buttonPanel, BorderLayout.SOUTH);

        replyDialog.setVisible(true);
    }
}
