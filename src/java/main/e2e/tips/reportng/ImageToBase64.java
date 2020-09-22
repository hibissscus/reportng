package e2e.tips.reportng;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageToBase64 {

    static String base64 = "iVBORw0KGgoAAAANSUhEUgAAAO0AAABuCAMAAAD8t2TLAAADAFBMVEX///8tVymRq45lh2GuxK3H\n" +
            "2MXW5NX7//ro8+fy/PLi6OLu9+3g7d/4//fx8/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
            "AAAAAAAAAAAAAAAiY5IsAAAMDElEQVR42u1cB3vbug4lwGXJVv7/76yb2BokH5dsLVK0m/be+yp8\n" +
            "baLBBQ4cHJAKIYcccsghhxxyyH9b5J8pSn5nRb/QQuka0hDSNKkWVUWKyqyysinUl06um440EpRU\n" +
            "u7lsSlINk/qemarhWQyDwTBQUA1D6y6WrVSypnq/NtWI3tRd8j3rO1d6Qbun2naX7VZt9KagfNpM\n" +
            "USnlNLatvz2Lkb0mRoPmN2LsRbVsL0PVDqD3+/ZmO3Y4J9VFV01Ru9nkWnzaVmm9Pyf4l571E7sT\n" +
            "5O750BE2yJtxxTCijXs5lofXZTHazY59ZUk/+bkloZqCdhOc1r4e7u2R1drPr/G2Uq7l2v8n6tKa\n" +
            "2AYzds6zTetWfp+Boa9pG5987k0sEX638b4dZtp9kXo+gqLD5Syavf4mZXfbvantrnQ6TILhMdSz\n" +
            "19D8pBsTkayXHezM0Hm6b+mRVam7ZbcxBcQ5KHTMF8oynbUogPZ+LKnF7XkmSGbMp8OPz/WQ7o2X\n" +
            "+4RToLwAuhh4aaa3aHNdkAKz+OoBNpaHFz/7N2GVh3xyF7JPjFd77X5jqB1Ml0A1jmqMt08lJQvv\n" +
            "fDnNXvey6IEkteW+A5PtjY5L86b7E8e3oFumDs5ESYzjzgD3DZD0+TL18XHeJMRNp5L2bsvJt5WV\n" +
            "TIMLm2gTlRSheUBlQ/3trro+X3IaSuqLu6Tei7Cm+BtWKoeNKznf7tNkwZOwkB3BtjW6qJyQby9d\n" +
            "8r0mpe3d1DZgI9lfBw5QjEy1h5F7Wf1mx0PVpVD6lrYBXvSuPyfNE0Mm9h/jhZTmlVZA3mn8LXjr\n" +
            "rQb1Nauy3E+PoWdoLbP13N0FoVdRiIE+BcUktCNmi8Ff8T+kjEZy35RWfImBj9nvwVXgvk1uvJXC\n" +
            "Uwr1nN1vziIHicEml3rb015prs2nobSV7a6yomslSSermH0vrrtm3UDnijp1BhHbtb0mimPX2uVr\n" +
            "09zTZLvNJ0gMrKB5cJvARhbjLC6UYKCrDv1k2kYZj4aUni2qufHP+gjZBJvrtu89auyDl4UNz+4y\n" +
            "uKCH/bXkbKFRhKsZyVhEorQGqSyiZVwAZduSTbClLZhS8CrCSFo2o4wcpkg/NVHxifLLTak8PBCl\n" +
            "X9KWl/PEb6Kb03rXoY2I/RhdNrNDt0tcDFw5Fn9YRrKfgbmwaHSTp9sv4m2LmOX3TxyAHYyDVyCw\n" +
            "T0JmaJAZ18SQp9uvehe9Mvx0wlEpOXMgHfa1p6crAZkR6RFZSZTIFYHUKwV0ZVMl6etzTWsfCUJ2\n" +
            "um1aXZuKFodh6NK8UfTR0qYjtRQWDkkDPnjKUGl5l7UPFnfCOhB1OqRpRMdqCSwTBLYVqAunktzs\n" +
            "ZD5ZPWo1iwjbGmsBP3vdEcpPCB0VlWJqrSwj/UlbVc+3NzxUPtg+t7Pr8gW2DNrxwQG/1G51YH31\n" +
            "z/2T3tAh40YPxlL8e86zUBSuzvG3S886EP3CwfA12gqF5kS3Egd7C3ztgfQGxL3Ak8nwTUbOEIku\n" +
            "8z9PI48U8CDrz1DNBh8MLFdkPQv3vkJK7XQKtJgvaTxtPE3mY3P4Nttn8o11+xxsI3GOiXQ08sH+\n" +
            "YediyGnXUYbAmTZZz8L9UN6iqRgBX2F652myHpuTiEkr/Z62EbyWsCBwng5DDDk5k4scm02Ts2hd\n" +
            "ey9CqTfZfMQ20s/sutbdOPDhMQsxZEjOoM68o+wqkn6SW835PjbvpyzSOxlwws4hQB/0kb7eQkwh\n" +
            "Hedv6Q7vhEUrVpF08KFMAdGnHWiuPHxPW78SgbkpyvDBzuGnx+KL9qB8OQ3k6uLjyK7JGYnczgPe\n" +
            "pd/H1lM7CaC6x/vJAuitZhDKp9o2a7CKQ7/Vft/MN6KOjl2HMGl1khs0/UFfH/HxFHfMx3ilC9Ba\n" +
            "DPVd3cQY9nj/2J32QQHZCFcXd+3Z1ii0RRapC3MM7B2Vl8J0BNNYKXqCpv6ZMTb9JjufeA7KgeRo\n" +
            "0x20Tu99A+TV7dsr6bDYuTHgHjQtH9aQy60LQClpX9NWKI2stTp3Ya2m8gSPhqf9JO5XQp/pDDc/\n" +
            "6VjByV887wOFg1a4ErD62dyjgW7arXaF7BueR37dWvjTvd+2mscTs6HcN/hvgGE1dnQEc7UwQSLU\n" +
            "oO+LKpfgFrMb/YZNNs0Yb+x+G8kzc/oeudeEzovtWShhC/bG7OYNbck99KCQLfm98qTv3eK+3fRa\n" +
            "RBsHOx3mfUnbEDiXrmtpf91b6/BGMHw7URgdXIB+96inG9NvO3AtFmPulPF559ZUXyD1PRc+t6zE\n" +
            "IO29cd1O4FCQJotgnqCZJ8HT5kKteXzco1DctI961JkqyTvFgGvBdLs08BbaY3teE1a66+sw0oFk\n" +
            "8v2lIZndpHGrd9roKmCm/+Eu55g97isvT4q5503x/i0scb+V4oYFS6EanBd0Sk74RqPGazoE7DGy\n" +
            "naGWAxG7jvTg2CrXeobZ470FX8NvMxqcxfY8C+HU0cr9wWVeTmn+SoGm+W3gy5NAvd+oZbZYdBL5\n" +
            "7oTPjvcNt7WK6WyEon3ibZvsItE5XrqInSU7zYUatE5RwgRGmtHiqnEbeIFalh0YMt1JlWYP+3Pa\n" +
            "xtx38mvSiGhPE5NkhZFxo1Z2r9bzK4xvzL3fV5wsPIIFTSbZqOgaI/vIIIu4mxFvxJLX2o65odmL\n" +
            "MVyrk/NNr6mx2yGeMQEsgh19O3YiLLKP2OvTPWxjq17jt5vRM4BqR1kPITITZz8xa6WSRswmcBuv\n" +
            "00Zi2Oc7WzPlssnFGaXHmahGzLaNxzM1r+/f+m4brOkAll8/lrHlKV9Drv5/UlmxTsCUR5LGznK3\n" +
            "H0tWQdO4T7vaN47Pm+ur2krUNifZyRcYW4by8WGf/64TeKw3ELB2k8DxbWx9E3Mt6KHYx9oL7Byq\n" +
            "YjsYGMDztI318cgVS2VbG0ZRfGZqxfj6rz11C4/L7PHf9Qav3wHX8ciVqhLZTKKmV/ltAD29t5/S\n" +
            "4rdsfa43eOd6DG9E1V7Rti2kEb+P589G7MUwotYvzuTCCH8LOyBXsr+78X7E1PB7HaxO8eoQ+abD\n" +
            "q2FWWuhiRzCUmfc8F8ZNvI+YOu68pd7P56wMZ2rOjgmeHyHa7U+i5qbCUHD7oLtf1piGt10jN/ZT\n" +
            "x/f35LySQipabb7XpjGdZK0gBrZc5vC+9tvKT2UZ6S6C3KrO1J9hf5l2TSdJTTv/4VJ+qZd9OOV9\n" +
            "oTci9Ixmj2TF09dNnpGen/fxfLNkgak6J46i8A+QVTI3tuELKC32gVr487qgX9RVCu+ZpPNZrm6J\n" +
            "oPhKrhFH+Qw+DDYbv7By5QLt0c02MLKrervC1WJDfWlqxKfRBR+bRXR7eSfv8TVNFsszsWGzoF4x\n" +
            "Nh0Jsx7XYBvdS5X3Lnw95rd98ih1EV81pcHZmBL8iRUkTMBr8eRt4/9dIvLavspXn+V6a2S64TXP\n" +
            "EQq9sDd3j6/5fd2Rr0Jx5Th6FsYNbTuBpaJmLnll1vN/5yMczEZxx/PGyUGExWtGA8M4cb/FerYm\n" +
            "mbKzf2GN8/kb3M2x2e/5y82vvW822iG2EjTn5ltNDvl3SEMOOeSQQw455JBDDjnkkEMOOaRQwp9i\n" +
            "wMIIDXPpagiBBXuNH/aeM0e9V2Sc/jklisMPygAa84gWjI8F0JJc4LamZx+yAGxfk1UV/4S2U9HP\n" +
            "gG9nSv6gQO3CvrWZBk6nN8bkqvgntaVAPzhwAbHzAR+XtducgHoj0x2RkTtMDx5TWtt1EXJSytw1\n" +
            "9yfo4mFl4P6zdj/GblPe/a0GdL/QncYE9me0hUq7WTmQatwMfFy2umomsxOe295gjPkwJ1yEe6GB\n" +
            "yWk31B89qMY8Fans68rgx4CG26FmBozVmatKcVX/7iVOg5EB9GNL68fYhsvadTgLYztaqWiEgH2A\n" +
            "vXD/YDK27ivB2j50Y1u7P2QUvv4bx5b60gAZfPiaqXtDMfxFHPxj69avV3KrV5cJEUT8QDssuLsW\n" +
            "uZ0E+4bAJnpvWePbA30eTySMl1+o6o/NHZyBfBEgdvR2Ps2obcqPdey8Jp9E+B1FdB1mh9wu8PpP\n" +
            "anvS13EVPi4l3rZPWrlpN8AP2yeDW9BJzPrBgV7N6vTHD6ah90UYh1U9VbSHr7/Cy/klHMb/kKLO\n" +
            "rH0QSv4SseALSP4aEcjJIYcccsgh/x/yPxrwBM55WR3TAAAAAElFTkSuQmCC";

    /**
     * Encode image to string
     *
     * @param image The image to encode
     * @param type  png, jpeg, bmp, ...
     * @return encoded string
     */
    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    /**
     * Decode string to image
     *
     * @param imageString The string to decode
     * @return decoded image
     */
    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            imageByte = decoder.decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
