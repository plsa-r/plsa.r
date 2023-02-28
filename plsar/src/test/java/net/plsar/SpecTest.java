package net.plsar;

import net.plsar.model.ViewCache;
import net.plsar.security.SecurityAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpecTest extends BaseTest {
    SecurityAttributes securityAttributes;

    public SpecTest(){
        this.securityAttributes = new SecurityAttributes("stargz.r", "secured");
    }

    @Test
    public void a() throws StargzrException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        ExperienceManager exp = new ExperienceManager();
        StringBuilder sb = new StringBuilder();
        sb.append("<stargzr:if spec=\"${tom.name == ''}\">\n");
        sb.append("    *!.\n");
        sb.append("</stargzr:if>\n");
        sb.append("<stargzr:foreach items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <stargzr:if spec=\"${tdo.title == 'Exercise *0'}\">\n");
        sb.append("         *ned.\n");
        sb.append("     </stargzr:if>\n");
        sb.append("     <stargzr:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         *jermaine.\n");
        sb.append("     </stargzr:if>\n");
        sb.append("</stargzr:foreach>\n");
        String result = exp.execute(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("*ned.*jermaine.", result);
    }

    @Test
    public void b() throws StargzrException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache resp = this.create();
        ExperienceManager exp = new ExperienceManager();
        StringBuilder sb = new StringBuilder();
        sb.append("<stargzr:if spec=\"${message != ''}\">\n");
        sb.append("${message}\n");
        sb.append("</stargzr:if>\n");
        String result = exp.execute(sb.toString(), resp, null, securityAttributes, new ArrayList<>()).trim();
        assertEquals("Effort.", result);
    }

    @Test
    public void c() throws StargzrException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<stargzr:if spec=\"${blank != ''}\">\n");
        sb.append("nothing.\n");
        sb.append("</stargzr:if>\n");
        String result = exp.execute(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).trim();
        assertEquals("", result);
    }

    @Test
    public void d() throws StargzrException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<stargzr:if spec=\"${tom.name == 'Tom'}\">\n");
        sb.append("Tom.\n");
        sb.append("</stargzr:if>\n");
        sb.append("<stargzr:if spec=\"${tom.wife.name == 'Penelope'}\">\n");
        sb.append("Penelope.\n");
        sb.append("</stargzr:if>\n");
        sb.append("<stargzr:if spec=\"${tom.wife.pet.name == 'Diego'}\">\n");
        sb.append("Diego.\n");
        sb.append("</stargzr:if>\n");
        String result = exp.execute(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("Tom.Penelope.Diego.", result);
    }

    @Test
    public void e() throws StargzrException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<stargzr:if spec=\"${not == null}\">\n");
        sb.append("not.\n");
        sb.append("</stargzr:if>\n");
        sb.append("<stargzr:if spec=\"${not != null}\">\n");
        sb.append("!not.\n");
        sb.append("</stargzr:if>\n");
        String result = exp.execute(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("not.", result);
    }

    @Test
    public void f() throws StargzrException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<stargzr:if spec=\"${todos.size() > 0}\">\n");
        sb.append(" <stargzr:foreach items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <stargzr:set var=\"selected\" val=\"\" \n");
        sb.append("     <stargzr:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         <stargzr:set var=\"selected\" val=\"selected\"\n");
        sb.append("     </stargzr:if>\n");
        sb.append("     :${selected}:\n");
        sb.append("     <stargzr:foreach items=\"${tdo.people}\" var=\"person\">\n");
        sb.append("         ${tdo.id} -> ${person.pet.name},\n");
        sb.append("     </stargzr:foreach>\n");
        sb.append(" </stargzr:foreach>\n");
        sb.append("</stargzr:if>\n");
        sb.append("<stargzr:if spec=\"${todos.size() == 0}\">\n");
        sb.append("     everyonealright.\n");
        sb.append("</stargzr:if>\n");
        String result = exp.execute(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("::0->Apache*0,0->Apache*1,0->Apache*2,:selected:1->Apache*0,1->Apache*1,1->Apache*2,::2->Apache*0,2->Apache*1,2->Apache*2,", result);
    }

    @Test
    public void g() throws StargzrException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        ExperienceManager exp = new ExperienceManager();
        sb.append("<stargzr:if spec=\"${tom.name == 'Tom'}\">\n");
        sb.append("     <stargzr:if spec=\"${condition}\">\n");
        sb.append("         condition.\n");
        sb.append("     </stargzr:if>\n");
        sb.append("</stargzr:if>\n");
        String result = exp.execute(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("condition.", result);
    }
}
