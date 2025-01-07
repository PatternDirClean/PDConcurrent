public
class a {
	public static
	void main(String[] args) {
		try {
			System.out.println(a());
		} catch ( Exception e ) {
			System.out.println("11111");
		}
	}

	private static
	String a() throws Exception {
		String a;
		try {
			a = "2";
			b();
		} finally {
			a = "3";
			System.out.println("11111");
		}
		return a;
	}

	private static
	void b() throws Exception {
		throw new Exception();
	}
}
