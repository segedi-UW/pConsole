import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

public abstract class Prog {

	protected Cons c;
	protected LoadHandler loadHandler;
	protected ClassLoader classLoader;

	protected Prog() {}

	public final void connect(Cons c, LoadHandler h, ClassLoader cl) {
		if (c == null) throw new NullPointerException("Cons is null");
		if (h == null) throw new NullPointerException("LoadHandler is null");
		if (cl == null) throw new NullPointerException("ClassHandler is null");
		this.c = c;
		this.loadHandler = h;
		this.classLoader = cl;
	}

	protected <T> Object loadSubclass(T obj, String subclassName, Object[] args) throws Throwable {
		//this.classLoader.loadClass(file.getName().substring(0, file.getName().length()-5));
		Class<?> sub = classLoader.loadClass(obj.getClass().getCanonicalName() + "$" + subclassName);
		Constructor<?> constructor = sub.getDeclaredConstructor(Object[].class);
		return constructor.newInstance(new Object[]{args});
	}

	public abstract void run(String[] args) throws Throwable;

}
