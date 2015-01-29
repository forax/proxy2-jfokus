import static java.lang.invoke.MethodHandles.publicLookup;

import java.io.PrintStream;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import com.github.forax.proxy2.Proxy2;
import com.github.forax.proxy2.Proxy2.ProxyContext;
import com.github.forax.proxy2.Proxy2.ProxyFactory;
import com.github.forax.proxy2.Proxy2.ProxyHandler;
import com.headius.invokebinder.Binder;

public interface Delegation {
  public void println(String message);
  
  public static void main(String[] args) {
    ProxyFactory<Delegation> factory =
      Proxy2.createAnonymousProxyFactory(Delegation.class,
        new Class<?>[] { PrintStream.class },
        new ProxyHandler.Default() { 
          @Override
          public boolean isMutable(int index, Class<?> type) {
            return false;
          }

          @Override
          public boolean override(Method method) {
            return false;
          }
          
          @Override
          public CallSite bootstrap(ProxyContext context) throws Throwable {
            MethodHandle target =
              Binder
                .from(context.type())
                .dropFirst()
                .invokeVirtual(publicLookup(), "println");
            return new ConstantCallSite(target);
          }
        });
    
    Delegation hello = factory.create(System.out);
    hello.println("hello proxy2");
  }
}
