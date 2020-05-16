package snippet;

public class Snippet {
	public static void main(String[] args) {
		sqlSessionFactory.setMapperLocations(resourcePatternResolver.getResources("classpath*:mapper/**/*.xml"));
	}
}

