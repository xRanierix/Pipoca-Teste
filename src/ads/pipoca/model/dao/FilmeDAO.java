package ads.pipoca.model.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ads.pipoca.model.entity.Filme;
import ads.pipoca.model.entity.Genero;

public class FilmeDAO {
	public int inserirFilme(Filme filme) throws IOException {
		int id = -1;
		String sql = "insert into Filme (titulo, descricao, diretor, posterpath, "
				+ "popularidade, data_lancamento, id_genero) values (?,?,?,?,?,?,?)";

		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement pst = conn.prepareStatement(sql);) {

			pst.setString(1, filme.getTitulo());
			pst.setString(2, filme.getDescricao());
			pst.setString(3, filme.getDiretor());
			pst.setString(4, filme.getPosterPath());
			pst.setDouble(5, filme.getPopularidade());
			pst.setDate(6, new java.sql.Date(filme.getDataLancamento().getTime())); //convertendo a data do java pro mysql
			pst.setInt(7, filme.getGenero().getId()); //pega o id do objeto genero que est� dentro do objeto filme
			pst.execute();

			// obter o id criado
			String query = "select LAST_INSERT_ID()"; //essa query puxa a id da �ltima coisa inserida
			try (PreparedStatement pst1 = conn.prepareStatement(query); 
					ResultSet rs = pst1.executeQuery();) {

				if (rs.next()) {
					id = rs.getInt(1);
					filme.setId(id);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		return id;
	}
	
	public Filme buscarFilme(int id) throws IOException {
		Filme filme = null;
		String sql = "select f.id, titulo, descricao, diretor, posterpath, "
				+ "popularidade, data_lancamento, id_genero, nome "
				+ "from filme f, genero g "
				+ "where f.id_genero = g.id and f.id = ?";

		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			
			pst.setInt(1, id);
			try (ResultSet rs = pst.executeQuery();) {

				while (rs.next()) {
					filme = new Filme();
					filme.setId(id);
					filme.setTitulo(rs.getString("titulo"));
					filme.setDescricao(rs.getString("descricao"));
					filme.setDiretor(rs.getString("diretor"));
					filme.setPosterPath(rs.getString("posterpath"));
					filme.setPopularidade(rs.getDouble("popularidade"));
					filme.setDataLancamento(rs.getDate("data_lancamento"));
					
					//Criar objeto genero
					Genero genero = new Genero();
					
					//Alimentou os campos Id e Nome no genero
					genero.setId(rs.getInt("id_genero"));
					genero.setNome(rs.getString("nome"));
					
					//filme recebe o genero alterado
					filme.setGenero(genero);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		return filme;
	}
	
	
	public Filme atualizarFilme(Filme filme) throws IOException { 
		
		String sql = "UPDATE filme "
				+ "SET titulo= ?, descricao= ?,diretor=?,posterpath=?, popularidade=?,data_lancamento=?,id_genero=? "
				+ "WHERE id= ?";
			
		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement pst = conn.prepareStatement(sql);) {

			pst.setString(1, filme.getTitulo());
			pst.setString(2, filme.getDescricao());
			pst.setString(3, filme.getDiretor());
			pst.setString(4, filme.getPosterPath());
			pst.setDouble(5, filme.getPopularidade());
			pst.setDate(6, new java.sql.Date(filme.getDataLancamento().getTime()));
			pst.setInt(7, filme.getGenero().getId());
			pst.setInt(8, filme.getId());
			pst.execute();

			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		
		Filme filmeTeste = new Filme();
		filmeTeste = buscarFilme(filme.getId());
		return filmeTeste;
	}
	
	public int excluirFilme(int id) throws IOException {
		String sql = "DELETE FROM filme WHERE id = ?";
		
		int result = -1;
		
		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement pst = conn.prepareStatement(sql);) {
			
			pst.setInt(1, id);
			pst.execute();
			result = 1;
			
		} catch (SQLException e) {	
			
			e.printStackTrace();
			throw new IOException(e);
		}	
		
		return result;
	}
	
	public ArrayList<Filme> listarFilmes() throws IOException { //criar array listarFilmes
		ArrayList<Filme> filmes = new ArrayList<>(); //instanciei objeto filmes na array
		String sql = "select f.id, titulo, descricao, diretor, posterpath, popularidade, data_lancamento, id_genero, nome from filme f, genero g where f.id_genero = g.id";
		
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery();) {

			while (rs.next()) {
				Filme filme = new Filme();
				filme.setId(rs.getInt("id"));
				filme.setTitulo(rs.getString("titulo"));
				filme.setDescricao(rs.getString("descricao"));
				filme.setDiretor(rs.getString("diretor"));
				filme.setPosterPath(rs.getString("posterpath"));
				filme.setPopularidade(rs.getDouble("popularidade"));
				filme.setDataLancamento(rs.getDate("data_lancamento"));
				Genero genero = new Genero();
				genero.setId(rs.getInt("id_genero"));
				genero.setNome(rs.getString("nome"));
				filme.setGenero(genero);
				
				filmes.add(filme); //adicionei o filme selecionado na array
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		return filmes; //objeto da array listarFilmes
	}
	
}

