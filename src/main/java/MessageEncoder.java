import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

class MessageEncoder extends JFrame {
    private final Random random;
    private final List<Character> characters;

    public MessageEncoder() {
        super("Message Encoder");
        setLayout(new FlowLayout());

        this.random = new Random();
        this.characters = new ArrayList<>();
        for (int i = 33; i < 127; i++) {
            if (!Character.isDigit((char) i)) {
                this.characters.add((char) i);
            }
        }

        JTextArea inputTextArea = new JTextArea(18, 60);
        inputTextArea.setBackground(Color.lightGray);
        inputTextArea.setLineWrap(true);
        inputTextArea.setAutoscrolls(false);
        JScrollPane inputScroll = new JScrollPane(inputTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(inputScroll);

        JTextArea outputTextArea = new JTextArea(18, 60);
        outputTextArea.setBackground(Color.lightGray);
        outputTextArea.setLineWrap(true);
        outputTextArea.setAutoscrolls(false);
        JScrollPane outputScroll = new JScrollPane(outputTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(outputScroll);

        JButton encodeButton = new JButton("Encode");
        add(encodeButton);
        JButton decodeButton = new JButton("Decode");
        add(decodeButton);
        JButton copyButton = new JButton("Copy");
        add(copyButton);

        encodeButton.addActionListener(e -> {
            String text = inputTextArea.getText();
            String encoded = encode(text);
            outputTextArea.setText(encoded);
        });

        decodeButton.addActionListener(e -> {
            String text = inputTextArea.getText();
            String decoded = decode(text);

            outputTextArea.setText(decoded);
            if (decoded.equals("Invalid input.")) {
                outputTextArea.setForeground(Color.RED);
            }
        });

        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(outputTextArea.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
    }

    private String encode(String text) {
        String base64encodedString = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        char[] chars = base64encodedString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= i;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            String numberString = String.format("%03d", (int) c);
            int i = 0;
            while (i < 3 && numberString.charAt(i) == '0') {
                sb.append(this.characters.get(this.random.nextInt(this.characters.size())));
                i++;
            }
            for (; i < 3; i++) {
                sb.append(numberString.charAt(i));
            }
        }
        return sb.toString();
    }

    private String decode(String text) {
        char[] chars = text.toCharArray();
        int n = chars.length;
        StringBuilder sb = new StringBuilder();
        String result;
        try {
            for (int i = 0, num = 0; i < n; i++) {
                if (Character.isDigit(chars[i])) {
                    num = num * 10 + (chars[i] - '0');
                }
                if (i % 3 == 2) {
                    sb.append((char) num);
                    num = 0;
                }
            }
            chars = sb.toString().toCharArray();
            for (int i = 0; i < chars.length; i++) {
                chars[i] ^= i;
            }
            result = new String(Base64.getDecoder().decode(new String(chars)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            result = "Invalid input.";
        }
        return result;
    }

    public static void main(String[] args) {
        MessageEncoder messageEncoder = new MessageEncoder();
        messageEncoder.setBackground(Color.lightGray);
        messageEncoder.setResizable(false);
        messageEncoder.setFocusable(false);
        messageEncoder.setSize(800, 700);
        messageEncoder.setVisible(true);
        messageEncoder.setLocationRelativeTo(null);
    }
}