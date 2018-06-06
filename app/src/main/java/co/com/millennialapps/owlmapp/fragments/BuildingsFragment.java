package co.com.millennialapps.owlmapp.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import co.com.millennialapps.owlmapp.models.Node;
import co.com.millennialapps.utils.firebase.FFirestoreManager;
import co.com.millennialapps.utils.tools.Preferences;
import co.com.millennialapps.owlmapp.R;
import co.com.millennialapps.owlmapp.activities.BuildDetailActivity;
import co.com.millennialapps.owlmapp.activities.MainActivity;
import co.com.millennialapps.owlmapp.adapters.RclBuildingsAdapter;
import co.com.millennialapps.owlmapp.models.Building;
import co.com.millennialapps.utils.tools.SearchBarHandler;

public class BuildingsFragment extends Fragment {

    private LinkedList<Building> buildings = new LinkedList<>();
    private RecyclerView rclBuildings;
    private RclBuildingsAdapter adapter;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_places, container, false);

        rclBuildings = view.findViewById(R.id.rclBuildings);
        adapter = new RclBuildingsAdapter(getActivity(), buildings,
                (building, context) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("building", new Gson().toJson(building));
                    Intent i = new Intent(context, BuildDetailActivity.class);
                    i.putExtras(bundle);
                    getActivity().startActivity(i);
                });

        FFirestoreManager.getInstance().get(getActivity(), "Nodes", task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                Node node = document.toObject(Node.class);
                node.setId(document.getId());
                if (!node.getType().equals("Camino")) {
                    Building building = new Building();
                    building.setId(node.getId());
                    building.setDescription(node.getDescription());
                    building.setLatitude(node.getLatitude());
                    building.setLongitude(node.getLongitude());
                    building.setName(node.getName());
                    building.setNumber(node.getNumber());
                    buildings.add(building);
                }
            }

            Collections.sort(buildings, (o1, o2) -> o1.getNumber().compareTo(o2.getNumber()));

            adapter.setBuildings(buildings);
        });
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rclBuildings.setLayoutManager(mLayoutManager);
        rclBuildings.setAdapter(adapter);

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.buildings, menu);
        this.menu = menu;

        handleBarIcons();

        ((MainActivity) getActivity()).setSbHandler(new SearchBarHandler(getActivity(), menu, R.id.action_search, R.string.search,
                null, R.color.colorPrimaryDark));
        ((MainActivity) getActivity()).getSbHandler().addChangeTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Preferences.saveSuggestion(getActivity(), query);
                query = query.toLowerCase();
                if (!((MainActivity) getActivity()).getSbHandler().getSearchView().isIconified()) {
                    ((MainActivity) getActivity()).getSbHandler().getSearchView().setIconified(true);
                }
                ((MainActivity) getActivity()).getSbHandler().close();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ((MainActivity) getActivity()).getSbHandler().populateAdapter(s);
                LinkedList<Building> filtered = new LinkedList<>();
                for (Building building : buildings) {
                    if (building.getName().toLowerCase().contains(s.toLowerCase())
                            || building.getNumber().contains(s)
                            || (building.getNumber() + " " + building.getName().toLowerCase()).contains(s.toLowerCase())) {
                        filtered.add(building);

                    }
                }

                adapter.setBuildings(filtered);

                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBarIcons() {
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(true);
    }
/*
    private void buildingsMap() {
        buildings.add(new Building("Construido para ser ocupado por las oficinas administrativas de la Universidad, ya que desde un principio, este edificio, ha sido considerado como el eje de la Institución, debido a su ubicación y altura.\n" +
                "Con sus once pisos, es sin duda, la edificación más alta del Campus Universitario, es la única edificación dentro de la zona universitaria que posee ascensor.", "101 Torre de Enfermería"));
        buildings.add(new Building("Es un sector indiscutible de convergencia para los estudiantes de la Ciudad Blanca.\n" +
                "Posee varias salas, en sus cuatro pisos y un sótano, entre las que podemos contar: la sala de referencia, la hemeroteca, y una especial para ciegos, etc.", "102 Biblioteca Central Gabriel García Márquez"));
        buildings.add(new Building("Construido para reunir bajo su estructura a la Cooperativa de Profesores y al Bienestar Estudiantil; actualmente se encuentra dotada de una amplia cafetería y cuenta con varias zonas de actividad deportiva.", "103 Polideportivo"));
        buildings.add(new Building("Edificio construido con el fin de promover el contacto de la Universidad con la cultura de nuestro tiempo. Posee una excelente acústica y un gran diseño; su auditorio cuenta con capacidad para 1631 asientos.Por ser un modelo de belleza arquitectónica recibió el Premio Nacional de Arquitectura en 1973. Debe su nombre al maestro colombiano León De Greiff, importante figura de la literatura colombiana.", "104 Auditorio León de Greiff"));
        buildings.add(new Building("Hace parte del grupo de las construcciones más antiguas del Campus Universitario.\n" +
                "Posee en su segundo piso un consultorio jurídico.\n" +
                "Ha presentado modificaciones en sus tres pisos.Gracias a su color, junto con otras edificaciones, generó el apelativo de \"Ciudad Blanca\"", "201 Facultad de Derecho, Ciencias Políticas y Sociales"));
        buildings.add(new Building("Construido con el fin de albergar las oficinas administrativas de la Facultad de Ciencias Humanas, y aunque en un principio compartieron el espacio aulas y oficinas, este edificio se ha relegado a ser centro administrativo y de conferencias.\n" +
                "Construcción perteneciente al grupo de las que rompieron con la unidad de acabados exteriores que hasta el momento habían prevalecido; a partir de ahora ya no es más la Ciudad Blanca.", "205 Edificio Orlando Fals Borda: Departamento de Sociología"));
        buildings.add(new Building("Construido para responder a las necesidades de elaboración de material bibliográfico; terminó siendo un lugar meritorio para rendir homenaje al arquitecto gestor de los pilares del Campus Universitario.\n" +
                "En su interior se exponen en forma permanente trabajos arquitectónicos y otras obras.", "207 Museo de Arquitectura Leopoldo Rother"));
        buildings.add(new Building("Inicialmente concebido para solucionar el espacio necesario para la ubicación de los alumnos que cursaban el año preparatorio, un año después (1953) se traslada a este sitio el Departamento de Economía y la Facultad de Odontología.\n" +
                "En 1961 se adapta como sede única de Odontología; donde permanece hasta ahora.", "210 Facultad de Odontología"));
        buildings.add(new Building("La razón principal de su construcción es la creación de espacios para la docencia teórica, en el área de Ciencias Humanas.\n" +
                "Fue dotada con 31 aulas, 3 auditorios y 2 laboratorios, distribuidos en cuatro pisos.\n" +
                "Fue construido con dineros correspondientes al empréstito BID - U.N. - 125.", "212 Aulas de Ciencias Humanas"));
        buildings.add(new Building("Creado con el fin de servir de residencia a los estudiantes que no fueran de Bogotá (119 cupos), se ubicó en este sitio ya que se encontraba cerca a la entrada principal.\n" +
                "Estuvo durante un tiempo fuera de servicio, hasta que se decidió cederla para que funcionaran en ella el Departamento de Agrícola y el de Lingüistica.\n" +
                "En el primer piso se adaptaron los laboratorios, en el segundo las aulas y las oficinas administrativas y docentes; los pisos tercero y cuarto cumplen diferentes funciones, en su mayoría académicas.", "214 Edificio Antonio Nariño - Departamento de Lingüística. Departamento de Ingeniería Civil y Agrícola"));
        buildings.add(new Building("Al igual que su construcción hermana (214) fue diseñada para que residieran estudiantes (61 cupos), contaba además con una gran cafetería; pero por el mal aprovechamiento que dieron de ella sus residentes, fue cerrada.\n" +
                "Posteriormente fue cedida a la Facultad de Artes y ésta la utiliza para desarrollar las actividades correspondientes al Departamento de Diseño Gráfico y al posgrado de Bellas Artes.\n" +
                "Consta de cuatro pisos en los cuales se reparten las aulas y oficinas que la ocupan.", "217 Edificio Francisco de Paula Santander: Diseño Gráfico"));
        buildings.add(new Building("Código que agrupa una serie de edificaciones entrelazadas e intercomunicadas que hacen de esta construcción la más amplia dentro del Campus Universitario.\n" +
                "Dentro de sus instalaciones se encuentran, distribuidas en sus tres pisos, tres facultades y un instituto.\n" +
                "Las facultades que allí se encuentran son:\n" +
                "-Facultad de Derecho, Ciencias Políticas y Sociales (consultorio jurídico, posgrado y la unidad de investigaciones).\n" +
                "-Facultad de Ciencias Humanas (Departamento de Literatura e Historia).\n" +
                "-Facultad de Ciencias (\"Geociencias\" y posgrados de Geofísica y Geología).\n" +
                "-Instituto de Biotecnología (cuenta con 12 laboratorios).", "224 Edificio Manuel Ancízar"));
        buildings.add(new Building("Es una de las edificaciones más nuevas, diseñado por el arquitecto Rogelio Salmona egresado de la Universidad y construido para albergar las dependencias de posgrado de la Facultad de Ciencias Humanas.\n" +
                "Posee una arquitectura moderna y altamente atractiva.\n" +
                "Diseñado y ejecutado con dineros propios de la Institución.", "225 Edificio Rogelio Salmona de Postgrados en Ciencias Humanas"));
        buildings.add(new Building("Aunque fueron construidas para formar parte de un conjunto de cuatro casas destinadas para ser vivienda de profesores.\n" +
                "Hizo parte del grupo con los cuales se inauguró la Ciudad Universitaria.\n" +
                "Recientemente modificada y acondicionada para servir de sede al Departamento de Filología e Idiomas.\n" +
                "En esta sede se dictan cursos de idiomas a estudiantes y particulares.", "229 Departamento de lenguas extranjeras"));
        buildings.add(new Building("Construido en la misma época que la edificación vecina (Aulas de Filología e Idiomas), este edificio desempeña en la actualidad la función de laboratorio y aula de instrucción teórica en el área de idiomas.\n" +
                "La obra de construcción fue ejecutada por el Ministerio de Obras Públicas.", "231 Idiomas"));
        buildings.add(new Building("Es un edificio, uno de tantos en la Universidad, que ha sido habitado por muchos ocupantes desde un principio, pero éste sin lugar a dudas es el que mayor cantidad de servicios ha prestado (10 ocupantes en total durante toda su historia).\n" +
                "Actualmente residen en su interior: la Facultad de Ciencias Económicas (en sus programas de pregrado y posgrado).", "238 Contaduria / Postgrados de Ciencias Económicas"));
        buildings.add(new Building("Uno de los edificios más pequeños de la \"Ciudad Blanca\", fue sede del museo de arte durante algunos años, hasta que cedieron sus instalaciones a la Facultad de Ciencias Humanas, la cual la utiliza para que en ella se desarrollen las actividades relacionadas con el Departamento de Filosofía.", "239 Filosofía"));
        buildings.add(new Building("Construida para cumplir con el objeto de satisfacer a las necesidades de la Comunidad Universitaria en materia de servicios religiosos.\n" +
                "Tiene una capacidad para 70 personas y cuenta con una zona para albergar a las personas religiosas que integran la comunidad católica.\n" +
                "Ofrece los servicios básicos religiosos.", "251 Capilla"));
        buildings.add(new Building("Construcción que aloja parte de los talleres de pintura, los de grabado, dibujo y fotografía, entre otras; además cuenta con una biblioteca y un salón de proyecciones.\n" +
                "- Posee dos pisos y en sus reformas la más importante es la creación de un mezzanine.", "301 Escuela de Artes Plásticas"));
        buildings.add(new Building("Construido en dos etapas, y diseñado por el arquitecto Hernán Herrera, gracias a que su proyecto fue el ganador del concurso público de arquitectura propuesto en la Facultad.\\Posee, en sus tres pisos, talleres, aulas, espacio para docentes y oficinas administrativas.", "303 Escuela de Arquitectura"));
        buildings.add(new Building("Desde un comienzo, este edificio fue diseñado con la finalidad específica de alojar las zonas de ensayo y ejecución de estudios musicales.\n" +
                "En la actualidad, en sus tres pisos, se encuentran además de estudios, aulas y salas de ensayo y audición, las oficinas administrativas del Departamento.", "305 Conservatorio de Música"));
        buildings.add(new Building("Construido para adaptar en sus dependencias talleres y aulas para diversos Departamentos, entre los que se destacan Economía y Construcción.\n" +
                "Es una pequeña construcción que cuenta con tres reducidos salones para instrucción académica y dos talleres de trabajo.", "309 Talleres y Aulas de Construcción"));
        buildings.add(new Building("Construido en tres etapas (años: 61, 68 y 70).\n" +
                "Inicialmente funcionó como un Departamento adscrito a la Facultad de Ciencias Humanas pero en 1978 fue constituido como una Facultad propia, la Facultad de Ciencias Económicas.", "310 Facultad de Ciencias Económicas"));
        buildings.add(new Building("Construcción ejecutada por la oficina de proyectos de la Facultad de Artes de la Universidad Nacional, esta edificación fue prevista para complementar los niveles académicos de la Facultad de Ciencias Económicas; proyectada para albergar las aulas, oficinas administrativas y de docentes que corresponden al área de posgrado.", "311 Bloque II Facultad de Ciencias Económicas"));
        buildings.add(new Building("Construido como sede de El Centro Interamericano de la Vivienda.\n" +
                "Desde un principio el inmueble fue dotado de aulas, talleres, oficinas y una biblioteca que a lo largo del tiempo se ha ido enriqueciendo con valiosa información sobre Desarrollo Urbano.", "314 Postgrados en Arquitectura - SINDU"));
        buildings.add(new Building("Construido, en dos etapas, como respuesta a las necesidades que la Institución tenia para dotar de unas instalaciones apropiadas que pudieran albergar obras, arte y otras muestras de expresión artística.", "317 Museo de Arte"));
        buildings.add(new Building("Fue uno de los edificios construidos con más altas especificaciones de acabados y muebles, con amplias áreas para oficinas y biblioteca.\n" +
                "Las oficinas se encuentran actualmente ocupadas por la Decanatura de la Facultad y por otros sectores administrativos.\n" +
                "Cuenta con tres pisos, salones de dibujo y una sala de informática.", "401 Facultad de Ingeniería / Edificio Viejo"));
        buildings.add(new Building("Inicialmente creado como dependencia de la Facultad de Ingeniería, cuenta con varios laboratorios de investigación, aulas estudiantiles y oficinas para profesores y personal administrativo.\n" +
                "Su construcción inicial era de dos pisos, pero posteriormente fue reformada a tres por el diseño del arquitecto Jairo Novoa.", "404 Departamentos de Matemáticas, Física y Estadística / Yu Takeuchi"));
        buildings.add(new Building("Esta edificación fue construida en el marco del programa para el desarrollo de la capacidad de investigación (BID - ICFES - UN).\n" +
                "Es una prolongación del edificio de la Facultad de Matemáticas.\n" +
                "Posee cuatro pisos y aulas suficientes para el desarrollo de proyectos de investigación en el área de posgrado.", "405 Postgrados en Matemáticas y Física"));
        buildings.add(new Building("Construido con el fin de albergar a los laboratorios de Ensayo de Materiales de la Facultad de Ingeniería.\n" +
                "Esta función se conserva hasta el presente, aunque ahora recibe el nombre de Instituto de Ensayo e Investigación (I.E.I.).\n" +
                "En la actualidad reside en sus instalaciones el Departamento de Ingeniería Civil.", "406 IEI / Instituto de Extensión e Investigación"));
        buildings.add(new Building("Esta edificación fue construida en el marco del programa para el desarrollo de la capacidad de investigación (BID - Icfes - UN); posee varios laboratorios especialmente acondicionados para los procesos que allí se desarrollan, también cuenta con salas para seminarios y una biblioteca especializada en procesos de manufactura.", "407 Postgrados en Materiales y Procesos de Manufactura"));
        buildings.add(new Building("Construido inicialmente como bodega del Ministerio de Obras Públicas bajo un contrato de comodato con la Universidad Nacional; luego de vencido el contrato la edificación pasó a ser parte de la Universidad, la cual remodeló la edificación acondicionándola para albergar los laboratorios de hidráulica.", "408 Laboratorio de Ensayos Hidráulicos"));
        buildings.add(new Building("Edificio anexo al 408, el cual fue dotado de un gran tanque en el sótano y de espacios libres muy amplios para la construcción de modelos a escala uno a uno de procesos hidráulicos.\n" +
                "Cuenta con tres pisos y un sótano en los cuales además de los laboratorios de ensayos presenta oficinas administrativas y de docencia.", "409 Laboratorio de Hidráulica"));
        buildings.add(new Building("Construcción realizada para ampliar la cobertura docente del sector de Ingeniería.\n" +
                "Para responder a dichos requerimientos, se construyó un grupo de talleres y laboratorios en las inmediaciones del edifico de Ensayo de Materiales.\n" +
                "Fue el primer edificio que se construyó dentro del Plan Cuatrienal de Desarrollo de la Universidad.", "411 Laboratorios de Ingeniería"));
        buildings.add(new Building("Construcción apodada \"La Casita en el Aire\", ya que venía planeándose desde el año 1970.\n" +
                "Se desarrollaron cuatro proyectos definitivos antes de efectuar los estudios técnicos.\n" +
                "En este espacio se encuentran las dependencias de la Maestría de Ingeniería Química y los laboratorios de Investigación necesarios para la Facultad.", "412 Laboratorio de Ingeniería Química"));
        buildings.add(new Building("La construcción de este observatorio Geofísico fue idea del doctor Belisario Ruiz Wilches, consistía en una cúpula de observación enchapada en madera; luego de unos años esta dependencia fue dotada con nuevos equipos de observación y una cúpula adicional.\n" +
                "Para mejorar su contexto académico recientemente fueron adicionadas nuevas aulas con capacidad para cincuenta estudiantes.", "413 Observatorio Astronómico"));
        buildings.add(new Building("Edificio creado para albergar la Facultad de Ciencias y en especial al Departamento de Biología.\n" +
                "Fue uno de los edificios más demorados en su construcción ya que se necesitaron varias etapas para poder realizarlo (cinco en total).\n" +
                "Durante los años 1972 a 1976 se realizó una remodelación total.\n" +
                "Inicialmente compartían espacio en este edificio Botánica y Biología.\n" +
                "Cuenta con una gran variedad de laboratorios entre los que se destacan: biotecnología vegetal, genética , ecología, fisiología animal y vegetal.", "421 Departamento de Biología"));
        buildings.add(new Building("Fue construido con el fin de alojar el Instituto de Ciencias Naturales y compartir su espacio con el Museo de Ciencias, algunos talleres y laboratorios, necesarios para otros Departamentos como son: Geociencia, Biología y Etnología.\n" +
                "Obra contratada a través de la Oficina Ejecutiva de Empréstitos y Construcciones de la Universidad Nacional.", "425 Instituto de Ciencias Naturales"));
        buildings.add(new Building("Construcción realizada bajo el marco del Programa para el Desarrollo de la Capacidad de Investigación (BID - Icfes - U.N.) de la institución.\n" +
                "Es el único edificio de \"La Ciudad Blanca\" con una estructura geométrica regular, ya que es una construcción realizada de forma cuadrada perfecta, con un espacio central abierto.", "426 Instituto de Genética"));
        buildings.add(new Building("Construido como un depósito de materiales para el Ministerio de Obras Públicas, es hoy el Almacén - Imprenta; encargado de elaborar los documentos y otras publicaciones de la Ciudad Universitaria y de sus integrantes.", "433 Almacén e Imprenta / Taller de Imprenta"));
        buildings.add(new Building("Forma parte de las muchas construcciones realizadas por el Ministerio de Obras Públicas para operar como depósito y talleres.\n" +
                "Inicialmente se encontraban divididos en el Instituto de Aplicación Pedagógica (431 - 1950) y la escuela \"Ramírez Montufar\" (434 - 1962).\n" +
                "Estas instituciones se fusionaron en 1991, creando el instituto, que ahora se conoce como \"IPARM\"", "434 Instituto Pedagógico Arturo Ramírez Montúfar IPARM"));
        buildings.add(new Building("Construido para permitir albergar y seleccionar los desechos sólidos que son despachados diariamente de las diferentes edificaciones del Campus Universitario; allí se realiza un proceso de separación de materiales (reciclables y no reciclables).", "437 Centro de Acopio de Residuos Sólidos"));
        buildings.add(new Building("Es uno de los pocos edificios que sólo han tenido cambios mínimos en su estructura; sus últimas modificaciones se hicieron en los años 2000 - 2001, en donde se mejoraron los laboratorios de esta dependencia.\n" +
                "Este edificio se encuentra comunicado con el de Química por una entrada lateral.", "450 Departamento de Farmacia"));
        buildings.add(new Building("El proyecto inicial fue concebido para funcionar como sede de las facultades de Química, Farmacia e Ingeniería Química.\n" +
                "Durante un corto tiempo contó con un invernadero en el costado nororiental.\n" +
                "Se han realizado ampliaciones en los laboratorios y la biblioteca; cuenta con 4 pisos en donde se distribuyen los auditorios, las aulas y los laboratorios.", "451 Departamento de Química"));
        buildings.add(new Building("Construido para mejorar los espacios de investigación necesarios para los posgrados que allí se realizan.\n" +
                "Fue diseñado en la Unidad de Planeación Física de la Universidad Nacional; construido en dos etapas: (1984 - 1987) estructura física y (1994) acabados cuarto y quinto piso.", "452 Postgrados en Bio/Química y Carbones"));
        buildings.add(new Building("Construido para solucionar los problemas locativos del área correspondiente a la Facultad de Ingeniería; este proyecto fue modificado cuando estaba en curso de obra, ya que se considero importante adicionar en el piso segundo la zona administrativa de cada una de los Departamentos adscritos a la Facultad.\n" +
                "Presenta en su estructura tres auditorios, ubicados en el primer piso.", "453 Aulas de Ingeniería"));
        buildings.add(new Building("El nuevo Edificio de Ciencia y Tecnología Luis Carlos Sarmiento Angulo, construido en la sede Bogotá de la Universidad Nacional de Colombia, fue hecho para inspirar y comprometer. A los estudiantes y docentes, para que asuman nuevos retos de innovación. A los egresados, para motivarlos a seguir aportándole a la institución que los educó. Y a la comunidad en general, para que crea mucho más en la labor que desarrolla esta Alma Mater.", "454 Ciencia y Tecnología (CyT)"));
        buildings.add(new Building("Construcción, de cinco pisos, que empieza a marcar la perdida de carácter urbanístico señalado desde un principio en la \"Ciudad Blanca\".\n" +
                "A partir de este momento la arquitectura no volvió a ser la misma, cuyo vuelco concluye con la construcción del edificio de Sociología.", "471 Facultad de Medicina"));
        buildings.add(new Building("Proyectado como el \"Instituto Botánico\", fue sede de los departamentos de Farmacia y Geología; al igual que de la Facultad de Agronomía.\n" +
                "En la actualidad pertenece a la Facultad de Ciencias, posee el museo de \"Geociencias\" y la biblioteca especializada de la Facultad.\n" +
                "Se encuentra la Decanatura, el taller del CEIF, las aulas de posgrado y pagaduría.", "476 Facultad de Ciencias"));
        buildings.add(new Building("Construido para albergar las aulas de Geociencias, es actualmente el edificio que colma las necesidades de sistematización que los estudiantes de la Universidad Nacional tienen.\n" +
                "Consta de aproximadamente 80 computadoras con acceso a Internet y posibilidad de impresión; el costo es mínimo y las instalaciones cómodas (posee dos pisos).\n" +
                "Posee un laboratorio de desarrollo informático.\n" +
                "Su uso se encuentra restringido para aquellos estudiantes que estén cursando materias virtuales de carácter semipresencial.", "477 Aulas de Informática"));
        buildings.add(new Building("Construido para albergar las aulas de Geociencias, es actualmente el edificio que colma las necesidades de sistematización que los estudiantes de la Universidad Nacional tienen.\n" +
                "Consta de aproximadamente 80 computadoras con acceso a Internet y posibilidad de impresión; el costo es mínimo y las instalaciones cómodas (posee dos pisos).\n" +
                "Posee un laboratorio de desarrollo informático.\n" +
                "Su uso se encuentra restringido para aquellos estudiantes que estén cursando materias virtuales de carácter semipresencial.", "481 Facultad de Medicina Veterinaria y Zootecnia"));
        buildings.add(new Building("Edificio construido con el fin de proveer una sede común a las facultades de Medicina, Veterinaria y Agronomía; formando así lo que se denominaría la Facultad de Ciencias Agropecuarias.\n" +
                "Pero el intento de integración fue un completo fracaso, y entonces la zona fue ocupada por dependencias académicas y administrativas de Agronomía. ", "500 Facultad de Ciencias Agrarias"));
        buildings.add(new Building("Construido por la dirección general del Icfes, la Universidad Nacional de Colombia cedió los terrenos y elaboró los proyectos para su construcción.\n" +
                "Su objetivo es consolidar un lugar que integre la información perteneciente a las Instituciones de Educación Superior.", "571 Hemeroteca Nacional"));
        buildings.add(new Building("Inicialmente construido para que compartieran espacio las oficinas administrativas y el Servicio Médico Estudiantil, posteriormente fue ocupada por la caja de Previsión Social, la Imprenta y algunas oficinas de deportes.\n" +
                "Actualmente se encuentran en su interior las oficinas y aulas académicas de la Facultad de Cine y Televisión.", "701 Departamento de Cine y Televisión"));
        buildings.add(new Building("Fue la primera zona construida del complejo de servicios deportivos planeados para la Ciudad Blanca.\n" +
                "Se proyectó, en el año 1965, como una fuente de ingresos, pero fue imposible lograr ese objetivo.\n" +
                "Cuenta con una capacidad para 8276 espectadores (distribuidos en dos espacios uno cubierto y otro no). En 1965 se adaptaron sus zonas de vestuario y baños, necesarios para lograr un mejor servicio.", "731 Estadio de fútbol Alfonso López Pumarejo"));
        buildings.add(new Building("Fue la segunda instalación de importancia en el campus universitario.\n" +
                "Cuenta con una gradería para 3000 espectadores.\n" +
                "Está adecuado para practicar varios deportes bajo su estructura; entre ellos patinaje, baloncesto, microfútbol y Balonvolea.\n" +
                "Tras varios intentos fallidos, finalmente en 1987 se ejecutó el proyecto de cubierta total del complejo deportivo.\n" +
                "Aquí también se encuentra el Museo de la Ciencia y el Juego.", "761 Concha Acústica"));
        buildings.add(new Building("Basada en una estructura en concreto, construida por el Ministerio de Obras Públicas para el Centro Administrativo Nacional (CAN) fue cedido a la Universidad Nacional para adaptar su estructura con el fin de albergar estudiantes casados.\n" +
                "Luego por algún tiempo permaneció fuera de uso hasta que finalmente se trasladó la sede administrativa y la rectoría de la Institución.", "861 Edificio Uriel Gutiérrez"));
        buildings.add(new Building("Estructura residencial concebida dentro del Programa de Bienestar Estudiantil.\n" +
                "Cuenta con 556 apartamentos los cuales fueron ocupados por estudiantes de intercambio y otros, su diseño se basó en la posibilidad de generar un ambiente familiar y seguro.\n" +
                "Durante su ocupación se realizaron una serie de cambios no aprobados, ejecutados por sus ocupantes, los cuales han alterado la hegemonía de la construcción.", "862 Unidad Camilo Torres"));
    }*/
}