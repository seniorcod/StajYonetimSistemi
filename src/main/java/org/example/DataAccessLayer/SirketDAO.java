package org.example.DataAccessLayer;

import org.example.Model.Sirket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SirketDAO {

    public List<Sirket> tumSirketleriGetir() {
        List<Sirket> liste = new ArrayList<>();
        String sql = "SELECT sirketid, sirketad FROM public.sirketler";

        try (Connection conn = DbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(new Sirket(
                        rs.getString("sirketid"),
                        rs.getString("sirketad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}
