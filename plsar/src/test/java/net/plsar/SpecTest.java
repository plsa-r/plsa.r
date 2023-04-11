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
    public void a() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        UserExperienceResolver exp = new UserExperienceResolver();
        StringBuilder sb = new StringBuilder();
        sb.append("<a:if spec=\"${tom.name == ''}\">\n");
        sb.append("    *!.\n");
        sb.append("</a:if>\n");
        sb.append("<a:foreach items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <a:if spec=\"${tdo.title == 'Exercise *0'}\">\n");
        sb.append("         *ned.\n");
        sb.append("     </a:if>\n");
        sb.append("     <a:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         *jermaine.\n");
        sb.append("     </a:if>\n");
        sb.append("</a:foreach>\n");
        String result = exp.resolve(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("*ned.*jermaine.", result);
    }

    @Test
    public void b() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache resp = this.create();
        UserExperienceResolver exp = new UserExperienceResolver();
        StringBuilder sb = new StringBuilder();
        sb.append("<a:if spec=\"${message != ''}\">\n");
        sb.append("${message}\n");
        sb.append("</a:if>\n");
        String result = exp.resolve(sb.toString(), resp, null, securityAttributes, new ArrayList<>()).trim();
        assertEquals("Effort.", result);
    }

    @Test
    public void c() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        UserExperienceResolver exp = new UserExperienceResolver();
        sb.append("<a:if spec=\"${blank != ''}\">\n");
        sb.append("nothing.\n");
        sb.append("</a:if>\n");
        String result = exp.resolve(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).trim();
        assertEquals("", result);
    }

    @Test
    public void d() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        UserExperienceResolver exp = new UserExperienceResolver();
        sb.append("<a:if spec=\"${tom.name == 'Tom'}\">\n");
        sb.append("Tom.\n");
        sb.append("</a:if>\n");
        sb.append("<a:if spec=\"${tom.wife.name == 'Penelope'}\">\n");
        sb.append("Penelope.\n");
        sb.append("</a:if>\n");
        sb.append("<a:if spec=\"${tom.wife.pet.name == 'Diego'}\">\n");
        sb.append("Diego.\n");
        sb.append("</a:if>\n");
        String result = exp.resolve(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("Tom.Penelope.Diego.", result);
    }

    @Test
    public void e() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        UserExperienceResolver exp = new UserExperienceResolver();
        sb.append("<a:if spec=\"${not == null}\">\n");
        sb.append("not.\n");
        sb.append("</a:if>\n");
        sb.append("<a:if spec=\"${not != null}\">\n");
        sb.append("!not.\n");
        sb.append("</a:if>\n");
        String result = exp.resolve(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("not.", result);
    }

    @Test
    public void f() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        UserExperienceResolver exp = new UserExperienceResolver();
        sb.append("<a:if spec=\"${todos.size() > 0}\">\n");
        sb.append(" <a:foreach items=\"${todos}\" var=\"tdo\">\n");
        sb.append("     <a:set var=\"selected\" val=\"\" \n");
        sb.append("     <a:if spec=\"${tdo.title == 'Exercise *1'}\">\n");
        sb.append("         <a:set var=\"selected\" val=\"selected\"\n");
        sb.append("     </a:if>\n");
        sb.append("     :${selected}:\n");
        sb.append("     <a:foreach items=\"${tdo.people}\" var=\"person\">\n");
        sb.append("         ${tdo.id} -> ${person.pet.name},\n");
        sb.append("     </a:foreach>\n");
        sb.append(" </a:foreach>\n");
        sb.append("</a:if>\n");
        sb.append("<a:if spec=\"${todos.size() == 0}\">\n");
        sb.append("     everyonealright.\n");
        sb.append("</a:if>\n");
        String result = exp.resolve(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("::0->Apache*0,0->Apache*1,0->Apache*2,:selected:1->Apache*0,1->Apache*1,1->Apache*2,::2->Apache*0,2->Apache*1,2->Apache*2,", result);
    }

    @Test
    public void g() throws PlsarException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ViewCache viewCache = this.create();
        StringBuilder sb = new StringBuilder();
        UserExperienceResolver exp = new UserExperienceResolver();
        sb.append("<a:if spec=\"${tom.name == 'Tom'}\">\n");
        sb.append("     <a:if spec=\"${condition}\">\n");
        sb.append("         condition.\n");
        sb.append("     </a:if>\n");
        sb.append("</a:if>\n");
        String result = exp.resolve(sb.toString(), viewCache, null, securityAttributes, new ArrayList<>()).replaceAll("([^\\S\\r\\n])+|(?:\\r?\\n)+", "");
        assertEquals("condition.", result);
    }
}
