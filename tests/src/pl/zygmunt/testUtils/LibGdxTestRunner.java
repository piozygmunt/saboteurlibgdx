package pl.zygmunt.testUtils;

import static org.mockito.Mockito.mock;

import java.lang.Thread.UncaughtExceptionHandler;

import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;


public class LibGdxTestRunner extends BlockJUnit4ClassRunner
{
   public LibGdxTestRunner(Class<?> klass) throws InitializationError
   {
      super(klass);
   }

   volatile boolean finished = false;


   @Override
   public void run(final RunNotifier notifier)
   {

      LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
      config.width = 300;
      config.height = 300;
      config.title = "Test";
      config.forceExit = false;
      Gdx.gl = mock(GL20.class);
      Gdx.gl20 = mock(GL20.class);
      final LibGdxTestRunner runner = this;
      LwjglApplication app = null;

      try
      {
         Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
         {

            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
               runner.finished = true;
               EachTestNotifier testNotifier = new EachTestNotifier(notifier, getDescription());
               testNotifier.addFailure(e);
            }
         });
         Gdx.gl = null;
         app = new LwjglApplication(new ApplicationListener()
         {

            @Override
            public void resume()
            {
            }


            @Override
            public void resize(int width, int height)
            {
            }


            @Override
            public void render()
            {
               if (!finished)
               {

                  try
                  {
                     runner.actualRun(notifier);
                  }
                  catch (Throwable t)
                  {
                  }
                  finally
                  {
                     runner.finished = true;
                  }
               }
            }


            @Override
            public void pause()
            {
            }


            @Override
            public void dispose()
            {
            }


            @Override
            public void create()
            {
            }
         }, config);

         while (!finished)
         {
            try
            {
               Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
         }
      }
      catch (Throwable t)
      {
       
      }
      finally
      {
         if (app != null)
         {
            app.stop();
         }
      }

   }


   public void actualRun(RunNotifier notifier)
   {
      super.run(notifier);
   }
   
   public static void simulateClick(final Actor actor)
   {
	   InputEvent event1 = new InputEvent();
       event1.setType(InputEvent.Type.touchDown);
       actor.fire(event1);
       
       InputEvent event2 = new InputEvent();
       event2.setType(InputEvent.Type.touchUp);
       actor.fire(event2);
   }
}