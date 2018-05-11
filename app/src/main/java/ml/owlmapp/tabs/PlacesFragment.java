package ml.owlmapp.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.HashMap;

public class PlacesFragment extends Fragment {

    private HashMap<String, Building> map = new HashMap<String, Building>();
    private SearchView mSearchView;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private ImageView mImage;
    private TextView mTextView, mTextView2;
    String[] sCheeseStrings = StringBuildings.strings;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_places, container, false);

        buildingsMap();
        mSearchView = view.findViewById(R.id.searchView);
        mTextView = view.findViewById(R.id.textView);
        mTextView2 = view.findViewById(R.id.textView2);
        mListView = view.findViewById(R.id.listView);
        mImage = view.findViewById(R.id.imageView);
        mListView.setAdapter(mAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, sCheeseStrings));
        mListView.setTextFilterEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mListView.setVisibility(View.INVISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mListView.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.INVISIBLE);
                mTextView2.setVisibility(View.INVISIBLE);
                mImage.setVisibility(View.INVISIBLE);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = mListView.getItemAtPosition(position);
                mListView.setVisibility(View.INVISIBLE);
                mSearchView.setQuery((CharSequence) listItem, true);
                String building = listItem.toString();
                Building b = map.get(building);
                mImage.setImageResource(b.getImage());
                mTextView.setText(b.getInfo());
                mTextView2.setText(building);
                mTextView.setVisibility(View.VISIBLE);
                mTextView2.setVisibility(View.VISIBLE);
                mImage.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void buildingsMap (){
        Building b;

        b = new Building(R.drawable.i101, "Construido para ser ocupado por las oficinas administrativas de la Universidad, ya que desde un principio, este edificio, ha sido considerado como el eje de la Institución, debido a su ubicación y altura.\n" +
                "Con sus once pisos, es sin duda, la edificación más alta del Campus Univ   ersitario, es la única edificación dentro de la zona universitaria que posee ascensor.");
        map.put("101 Torre de Enfermería", b);
        b = new Building(R.drawable.i102, "Es un sector indiscutible de convergencia para los estudiantes de la Ciudad Blanca.\n" +
                "Posee varias salas, en sus cuatro pisos y un sótano, entre las que podemos contar: la sala de referencia, la hemeroteca, y una especial para ciegos, etc.");
        map.put("102 Biblioteca Central Gabriel García Márquez", b);
        b = new Building(R.drawable.i103, "Construido para reunir bajo su estructura a la Cooperativa de Profesores y al Bienestar Estudiantil; actualmente se encuentra dotada de una amplia cafetería y cuenta con varias zonas de actividad deportiva.");
        map.put("103 Polideportivo", b);
        b = new Building(R.drawable.i104, "Edificio construido con el fin de promover el contacto de la Universidad con la cultura de nuestro tiempo. Posee una excelente acústica y un gran diseño; su auditorio cuenta con capacidad para 1631 asientos.Por ser un modelo de belleza arquitectónica recibió el Premio Nacional de Arquitectura en 1973. Debe su nombre al maestro colombiano León De Greiff, importante figura de la literatura colombiana.");
        map.put("104 Auditorio León de Greiff", b);
        b = new Building(R.drawable.i201, "Hace parte del grupo de las construcciones más antiguas del Campus Universitario.\n" +
                "Posee en su segundo piso un consultorio jurídico.\n" +
                "Ha presentado modificaciones en sus tres pisos.Gracias a su color, junto con otras edificaciones, generó el apelativo de \"Ciudad Blanca\"");
        map.put("201 Facultad de Derecho, Ciencias Políticas y Sociales", b);
        b = new Building(R.drawable.i205, "Construido con el fin de albergar las oficinas administrativas de la Facultad de Ciencias Humanas, y aunque en un principio compartieron el espacio aulas y oficinas, este edificio se ha relegado a ser centro administrativo y de conferencias.\n" +
                "Construcción perteneciente al grupo de las que rompieron con la unidad de acabados exteriores que hasta el momento habían prevalecido; a partir de ahora ya no es más la Ciudad Blanca.");
        map.put("205 Edificio Orlando Fals Borda: Departamento de Sociología", b);
        b = new Building(R.drawable.i207, "Construido para responder a las necesidades de elaboración de material bibliográfico; terminó siendo un lugar meritorio para rendir homenaje al arquitecto gestor de los pilares del Campus Universitario.\n" +
                "En su interior se exponen en forma permanente trabajos arquitectónicos y otras obras.");
        map.put("207 Museo de Arquitectura Leopoldo Rother", b);
        b = new Building(R.drawable.i210, "Inicialmente concebido para solucionar el espacio necesario para la ubicación de los alumnos que cursaban el año preparatorio, un año después (1953) se traslada a este sitio el Departamento de Economía y la Facultad de Odontología.\n" +
                "En 1961 se adapta como sede única de Odontología; donde permanece hasta ahora.");
        map.put("210 Facultad de Odontología", b);
        b = new Building(R.drawable.i212, "La razón principal de su construcción es la creación de espacios para la docencia teórica, en el área de Ciencias Humanas.\n" +
                "Fue dotada con 31 aulas, 3 auditorios y 2 laboratorios, distribuidos en cuatro pisos.\n" +
                "Fue construido con dineros correspondientes al empréstito BID - U.N. - 125.");
        map.put("212 Aulas de Ciencias Humanas", b);
        b = new Building(R.drawable.i214, "Creado con el fin de servir de residencia a los estudiantes que no fueran de Bogotá (119 cupos), se ubicó en este sitio ya que se encontraba cerca a la entrada principal.\n" +
                "Estuvo durante un tiempo fuera de servicio, hasta que se decidió cederla para que funcionaran en ella el Departamento de Agrícola y el de Lingüistica.\n" +
                "En el primer piso se adaptaron los laboratorios, en el segundo las aulas y las oficinas administrativas y docentes; los pisos tercero y cuarto cumplen diferentes funciones, en su mayoría académicas.");
        map.put("214 Edificio Antonio Nariño - Departamento de Lingüística. Departamento de Ingeniería Civil y Agrícola", b);
        b = new Building(R.drawable.i217, "Al igual que su construcción hermana (214) fue diseñada para que residieran estudiantes (61 cupos), contaba además con una gran cafetería; pero por el mal aprovechamiento que dieron de ella sus residentes, fue cerrada.\n" +
                "Posteriormente fue cedida a la Facultad de Artes y ésta la utiliza para desarrollar las actividades correspondientes al Departamento de Diseño Gráfico y al posgrado de Bellas Artes.\n" +
                "Consta de cuatro pisos en los cuales se reparten las aulas y oficinas que la ocupan.");
        map.put("217 Edificio Francisco de Paula Santander: Diseño Gráfico", b);
        b = new Building(R.drawable.i224, "Código que agrupa una serie de edificaciones entrelazadas e intercomunicadas que hacen de esta construcción la más amplia dentro del Campus Universitario.\n" +
                "Dentro de sus instalaciones se encuentran, distribuidas en sus tres pisos, tres facultades y un instituto.\n" +
                "Las facultades que allí se encuentran son:\n" +
                "-Facultad de Derecho, Ciencias Políticas y Sociales (consultorio jurídico, posgrado y la unidad de investigaciones).\n" +
                "-Facultad de Ciencias Humanas (Departamento de Literatura e Historia).\n" +
                "-Facultad de Ciencias (\"Geociencias\" y posgrados de Geofísica y Geología).\n" +
                "-Instituto de Biotecnología (cuenta con 12 laboratorios).");
        map.put("224 Edificio Manuel Ancízar", b);
        b = new Building(R.drawable.i225, "Es una de las edificaciones más nuevas, diseñado por el arquitecto Rogelio Salmona egresado de la Universidad y construido para albergar las dependencias de posgrado de la Facultad de Ciencias Humanas.\n" +
                "Posee una arquitectura moderna y altamente atractiva.\n" +
                "Diseñado y ejecutado con dineros propios de la Institución.");
        map.put("225 Edificio Rogelio Salmona de Postgrados en Ciencias Humanas", b);
        b = new Building(R.drawable.i229, "Aunque fueron construidas para formar parte de un conjunto de cuatro casas destinadas para ser vivienda de profesores.\n" +
                "Hizo parte del grupo con los cuales se inauguró la Ciudad Universitaria.\n" +
                "Recientemente modificada y acondicionada para servir de sede al Departamento de Filología e Idiomas.\n" +
                "En esta sede se dictan cursos de idiomas a estudiantes y particulares.");
        map.put("229 Departamento de lenguas extranjeras", b);
        b = new Building(0, "Construido en la misma época que la edificación vecina (Aulas de Filología e Idiomas), este edificio desempeña en la actualidad la función de laboratorio y aula de instrucción teórica en el área de idiomas.\n" +
                "La obra de construcción fue ejecutada por el Ministerio de Obras Públicas.");
        map.put("231 Idiomas", b);
        b = new Building(0, "Es un edificio, uno de tantos en la Universidad, que ha sido habitado por muchos ocupantes desde un principio, pero éste sin lugar a dudas es el que mayor cantidad de servicios ha prestado (10 ocupantes en total durante toda su historia).\n" +
                "Actualmente residen en su interior: la Facultad de Ciencias Económicas (en sus programas de pregrado y posgrado).");
        map.put("238 Contaduria / Postgrados de Ciencias Económicas", b);
        b = new Building(R.drawable.i239, "Uno de los edificios más pequeños de la \"Ciudad Blanca\", fue sede del museo de arte durante algunos años, hasta que cedieron sus instalaciones a la Facultad de Ciencias Humanas, la cual la utiliza para que en ella se desarrollen las actividades relacionadas con el Departamento de Filosofía.");
        map.put("239 Filosofía", b);
        b = new Building(R.drawable.i251, "Construida para cumplir con el objeto de satisfacer a las necesidades de la Comunidad Universitaria en materia de servicios religiosos.\n" +
                "Tiene una capacidad para 70 personas y cuenta con una zona para albergar a las personas religiosas que integran la comunidad católica.\n" +
                "Ofrece los servicios básicos religiosos.");
        map.put("251 Capilla", b);
        b = new Building(R.drawable.i301, "Construcción que aloja parte de los talleres de pintura, los de grabado, dibujo y fotografía, entre otras; además cuenta con una biblioteca y un salón de proyecciones.\n" +
                "- Posee dos pisos y en sus reformas la más importante es la creación de un mezzanine.");
        map.put("301 Escuela de Artes Plásticas", b);
        b = new Building(R.drawable.i303, "Construido en dos etapas, y diseñado por el arquitecto Hernán Herrera, gracias a que su proyecto fue el ganador del concurso público de arquitectura propuesto en la Facultad.\\Posee, en sus tres pisos, talleres, aulas, espacio para docentes y oficinas administrativas.");
        map.put("303 Escuela de Arquitectura", b);
        b = new Building(R.drawable.i305, "Desde un comienzo, este edificio fue diseñado con la finalidad específica de alojar las zonas de ensayo y ejecución de estudios musicales.\n" +
                "En la actualidad, en sus tres pisos, se encuentran además de estudios, aulas y salas de ensayo y audición, las oficinas administrativas del Departamento.");
        map.put("305 Conservatorio de Música", b);
        b = new Building(0, "Construido para adaptar en sus dependencias talleres y aulas para diversos Departamentos, entre los que se destacan Economía y Construcción.\n" +
                "Es una pequeña construcción que cuenta con tres reducidos salones para instrucción académica y dos talleres de trabajo.");
        map.put("309 Talleres y Aulas de Construcción", b);
        b = new Building(R.drawable.i310, "Construido en tres etapas (años: 61, 68 y 70).\n" +
                "Inicialmente funcionó como un Departamento adscrito a la Facultad de Ciencias Humanas pero en 1978 fue constituido como una Facultad propia, la Facultad de Ciencias Económicas.");
        map.put("310 Facultad de Ciencias Económicas", b);
        b = new Building(R.drawable.i311, "Construcción ejecutada por la oficina de proyectos de la Facultad de Artes de la Universidad Nacional, esta edificación fue prevista para complementar los niveles académicos de la Facultad de Ciencias Económicas; proyectada para albergar las aulas, oficinas administrativas y de docentes que corresponden al área de posgrado.");
        map.put("311 Bloque II Facultad de Ciencias Económicas", b);
        b = new Building(R.drawable.i314, "Construido como sede de El Centro Interamericano de la Vivienda.\n" +
                "Desde un principio el inmueble fue dotado de aulas, talleres, oficinas y una biblioteca que a lo largo del tiempo se ha ido enriqueciendo con valiosa información sobre Desarrollo Urbano.");
        map.put("314 Postgrados en Arquitectura - SINDU", b);
        b = new Building(R.drawable.i317, "Construido, en dos etapas, como respuesta a las necesidades que la Institución tenia para dotar de unas instalaciones apropiadas que pudieran albergar obras, arte y otras muestras de expresión artística.");
        map.put("317 Museo de Arte", b);
        b = new Building(R.drawable.i401, "Fue uno de los edificios construidos con más altas especificaciones de acabados y muebles, con amplias áreas para oficinas y biblioteca.\n" +
                "Las oficinas se encuentran actualmente ocupadas por la Decanatura de la Facultad y por otros sectores administrativos.\n" +
                "Cuenta con tres pisos, salones de dibujo y una sala de informática.");
        map.put("401 Facultad de Ingeniería / Edificio Viejo", b);
        b = new Building(R.drawable.i404, "Inicialmente creado como dependencia de la Facultad de Ingeniería, cuenta con varios laboratorios de investigación, aulas estudiantiles y oficinas para profesores y personal administrativo.\n" +
                "Su construcción inicial era de dos pisos, pero posteriormente fue reformada a tres por el diseño del arquitecto Jairo Novoa.");
        map.put("404 Departamentos de Matemáticas, Física y Estadística / Yu Takeuchi", b);
        b = new Building(R.drawable.i405, "Esta edificación fue construida en el marco del programa para el desarrollo de la capacidad de investigación (BID - ICFES - UN).\n" +
                "Es una prolongación del edificio de la Facultad de Matemáticas.\n" +
                "Posee cuatro pisos y aulas suficientes para el desarrollo de proyectos de investigación en el área de posgrado.");
        map.put("405 Postgrados en Matemáticas y Física", b);
        b = new Building(R.drawable.i406, "Construido con el fin de albergar a los laboratorios de Ensayo de Materiales de la Facultad de Ingeniería.\n" +
                "Esta función se conserva hasta el presente, aunque ahora recibe el nombre de Instituto de Ensayo e Investigación (I.E.I.).\n" +
                "En la actualidad reside en sus instalaciones el Departamento de Ingeniería Civil.");
        map.put("406 IEI / Instituto de Extensión e Investigación", b);
        b = new Building(0, "Esta edificación fue construida en el marco del programa para el desarrollo de la capacidad de investigación (BID - Icfes - UN); posee varios laboratorios especialmente acondicionados para los procesos que allí se desarrollan, también cuenta con salas para seminarios y una biblioteca especializada en procesos de manufactura.");
        map.put("407 Postgrados en Materiales y Procesos de Manufactura", b);
        b = new Building(R.drawable.i408, "Construido inicialmente como bodega del Ministerio de Obras Públicas bajo un contrato de comodato con la Universidad Nacional; luego de vencido el contrato la edificación pasó a ser parte de la Universidad, la cual remodeló la edificación acondicionándola para albergar los laboratorios de hidráulica.");
        map.put("408 Laboratorio de Ensayos Hidráulicos", b);
        b = new Building(R.drawable.i409, "Edificio anexo al 408, el cual fue dotado de un gran tanque en el sótano y de espacios libres muy amplios para la construcción de modelos a escala uno a uno de procesos hidráulicos.\n" +
                "Cuenta con tres pisos y un sótano en los cuales además de los laboratorios de ensayos presenta oficinas administrativas y de docencia.");
        map.put("409 Laboratorio de Hidráulica", b);
        b = new Building(0, "Construcción realizada para ampliar la cobertura docente del sector de Ingeniería.\n" +
                "Para responder a dichos requerimientos, se construyó un grupo de talleres y laboratorios en las inmediaciones del edifico de Ensayo de Materiales.\n" +
                "Fue el primer edificio que se construyó dentro del Plan Cuatrienal de Desarrollo de la Universidad.");
        map.put("411 Laboratorios de Ingeniería", b);
        b = new Building(0, "Construcción apodada \"La Casita en el Aire\", ya que venía planeándose desde el año 1970.\n" +
                "Se desarrollaron cuatro proyectos definitivos antes de efectuar los estudios técnicos.\n" +
                "En este espacio se encuentran las dependencias de la Maestría de Ingeniería Química y los laboratorios de Investigación necesarios para la Facultad.");
        map.put("412 Laboratorio de Ingeniería Química", b);
        b = new Building(R.drawable.i413, "La construcción de este observatorio Geofísico fue idea del doctor Belisario Ruiz Wilches, consistía en una cúpula de observación enchapada en madera; luego de unos años esta dependencia fue dotada con nuevos equipos de observación y una cúpula adicional.\n" +
                "Para mejorar su contexto académico recientemente fueron adicionadas nuevas aulas con capacidad para cincuenta estudiantes.");
        map.put("413 Observatorio Astronómico", b);
        b = new Building(R.drawable.i421, "Edificio creado para albergar la Facultad de Ciencias y en especial al Departamento de Biología.\n" +
                "Fue uno de los edificios más demorados en su construcción ya que se necesitaron varias etapas para poder realizarlo (cinco en total).\n" +
                "Durante los años 1972 a 1976 se realizó una remodelación total.\n" +
                "Inicialmente compartían espacio en este edificio Botánica y Biología.\n" +
                "Cuenta con una gran variedad de laboratorios entre los que se destacan: biotecnología vegetal, genética , ecología, fisiología animal y vegetal.");
        map.put("421 Departamento de Biología", b);
        b = new Building(R.drawable.i425, "Fue construido con el fin de alojar el Instituto de Ciencias Naturales y compartir su espacio con el Museo de Ciencias, algunos talleres y laboratorios, necesarios para otros Departamentos como son: Geociencia, Biología y Etnología.\n" +
                "Obra contratada a través de la Oficina Ejecutiva de Empréstitos y Construcciones de la Universidad Nacional.");
        map.put("425 Instituto de Ciencias Naturales", b);
        b = new Building(R.drawable.i426, "Construcción realizada bajo el marco del Programa para el Desarrollo de la Capacidad de Investigación (BID - Icfes - U.N.) de la institución.\n" +
                "Es el único edificio de \"La Ciudad Blanca\" con una estructura geométrica regular, ya que es una construcción realizada de forma cuadrada perfecta, con un espacio central abierto.");
        map.put("426 Instituto de Genética", b);
        b = new Building(0, "Construido como un depósito de materiales para el Ministerio de Obras Públicas, es hoy el Almacén - Imprenta; encargado de elaborar los documentos y otras publicaciones de la Ciudad Universitaria y de sus integrantes.");
        map.put("433 Almacén e Imprenta / Taller de Imprenta", b);
        b = new Building(R.drawable.i434, "Forma parte de las muchas construcciones realizadas por el Ministerio de Obras Públicas para operar como depósito y talleres.\n" +
                "Inicialmente se encontraban divididos en el Instituto de Aplicación Pedagógica (431 - 1950) y la escuela \"Ramírez Montufar\" (434 - 1962).\n" +
                "Estas instituciones se fusionaron en 1991, creando el instituto, que ahora se conoce como \"IPARM\"");
        map.put("434 Instituto Pedagógico Arturo Ramírez Montúfar IPARM", b);
        b = new Building(0, "Construido para permitir albergar y seleccionar los desechos sólidos que son despachados diariamente de las diferentes edificaciones del Campus Universitario; allí se realiza un proceso de separación de materiales (reciclables y no reciclables).");
        map.put("437 Centro de Acopio de Residuos Sólidos", b);
        b = new Building(R.drawable.i450, "Es uno de los pocos edificios que sólo han tenido cambios mínimos en su estructura; sus últimas modificaciones se hicieron en los años 2000 - 2001, en donde se mejoraron los laboratorios de esta dependencia.\n" +
                "Este edificio se encuentra comunicado con el de Química por una entrada lateral.");
        map.put("450 Departamento de Farmacia", b);
        b = new Building(R.drawable.i451, "El proyecto inicial fue concebido para funcionar como sede de las facultades de Química, Farmacia e Ingeniería Química.\n" +
                "Durante un corto tiempo contó con un invernadero en el costado nororiental.\n" +
                "Se han realizado ampliaciones en los laboratorios y la biblioteca; cuenta con 4 pisos en donde se distribuyen los auditorios, las aulas y los laboratorios.");
        map.put("451 Departamento de Química", b);
        b = new Building(0, "Construido para mejorar los espacios de investigación necesarios para los posgrados que allí se realizan.\n" +
                "Fue diseñado en la Unidad de Planeación Física de la Universidad Nacional; construido en dos etapas: (1984 - 1987) estructura física y (1994) acabados cuarto y quinto piso.");
        map.put("452 Postgrados en Bio/Química y Carbones", b);
        b = new Building(R.drawable.i453, "Construido para solucionar los problemas locativos del área correspondiente a la Facultad de Ingeniería; este proyecto fue modificado cuando estaba en curso de obra, ya que se considero importante adicionar en el piso segundo la zona administrativa de cada una de los Departamentos adscritos a la Facultad.\n" +
                "Presenta en su estructura tres auditorios, ubicados en el primer piso.");
        map.put("453 Aulas de Ingeniería", b);
        b = new Building (R.drawable.i454, "El nuevo Edificio de Ciencia y Tecnología Luis Carlos Sarmiento Angulo, construido en la sede Bogotá de la Universidad Nacional de Colombia, fue hecho para inspirar y comprometer. A los estudiantes y docentes, para que asuman nuevos retos de innovación. A los egresados, para motivarlos a seguir aportándole a la institución que los educó. Y a la comunidad en general, para que crea mucho más en la labor que desarrolla esta Alma Mater.");
        map.put("454 Ciencia y Tecnología (CyT)", b);
        b = new Building(R.drawable.i471, "Construcción, de cinco pisos, que empieza a marcar la perdida de carácter urbanístico señalado desde un principio en la \"Ciudad Blanca\".\n" +
                "A partir de este momento la arquitectura no volvió a ser la misma, cuyo vuelco concluye con la construcción del edificio de Sociología.");
        map.put("471 Facultad de Medicina", b);
        b = new Building(R.drawable.i476, "Proyectado como el \"Instituto Botánico\", fue sede de los departamentos de Farmacia y Geología; al igual que de la Facultad de Agronomía.\n" +
                "En la actualidad pertenece a la Facultad de Ciencias, posee el museo de \"Geociencias\" y la biblioteca especializada de la Facultad.\n" +
                "Se encuentra la Decanatura, el taller del CEIF, las aulas de posgrado y pagaduría.");
        map.put("476 Facultad de Ciencias", b);
        b = new Building(0, "Construido para albergar las aulas de Geociencias, es actualmente el edificio que colma las necesidades de sistematización que los estudiantes de la Universidad Nacional tienen.\n" +
                "Consta de aproximadamente 80 computadoras con acceso a Internet y posibilidad de impresión; el costo es mínimo y las instalaciones cómodas (posee dos pisos).\n" +
                "Posee un laboratorio de desarrollo informático.\n" +
                "Su uso se encuentra restringido para aquellos estudiantes que estén cursando materias virtuales de carácter semipresencial.");
        map.put("477 Aulas de Informática", b);
        b = new Building(R.drawable.i481, "Construido para albergar las aulas de Geociencias, es actualmente el edificio que colma las necesidades de sistematización que los estudiantes de la Universidad Nacional tienen.\n" +
                "Consta de aproximadamente 80 computadoras con acceso a Internet y posibilidad de impresión; el costo es mínimo y las instalaciones cómodas (posee dos pisos).\n" +
                "Posee un laboratorio de desarrollo informático.\n" +
                "Su uso se encuentra restringido para aquellos estudiantes que estén cursando materias virtuales de carácter semipresencial.");
        map.put("481 Facultad de Medicina Veterinaria y Zootecnia", b);
        b = new Building(R.drawable.i500, "Edificio construido con el fin de proveer una sede común a las facultades de Medicina, Veterinaria y Agronomía; formando así lo que se denominaría la Facultad de Ciencias Agropecuarias.\n" +
                "Pero el intento de integración fue un completo fracaso, y entonces la zona fue ocupada por dependencias académicas y administrativas de Agronomía. ");
        map.put("500 Facultad de Ciencias Agrarias", b);
        b = new Building(R.drawable.i571, "Construido por la dirección general del Icfes, la Universidad Nacional de Colombia cedió los terrenos y elaboró los proyectos para su construcción.\n" +
                "Su objetivo es consolidar un lugar que integre la información perteneciente a las Instituciones de Educación Superior.");
        map.put("571 Hemeroteca Nacional", b);
        b = new Building(R.drawable.i701, "Inicialmente construido para que compartieran espacio las oficinas administrativas y el Servicio Médico Estudiantil, posteriormente fue ocupada por la caja de Previsión Social, la Imprenta y algunas oficinas de deportes.\n" +
                "Actualmente se encuentran en su interior las oficinas y aulas académicas de la Facultad de Cine y Televisión.");
        map.put("701 Departamento de Cine y Televisión", b);
        b = new Building(R.drawable.i731, "Fue la primera zona construida del complejo de servicios deportivos planeados para la Ciudad Blanca.\n" +
                "Se proyectó, en el año 1965, como una fuente de ingresos, pero fue imposible lograr ese objetivo.\n" +
                "Cuenta con una capacidad para 8276 espectadores (distribuidos en dos espacios uno cubierto y otro no). En 1965 se adaptaron sus zonas de vestuario y baños, necesarios para lograr un mejor servicio.");
        map.put("731 Estadio de fútbol Alfonso López Pumarejo", b);
        b = new Building(R.drawable.i761, "Fue la segunda instalación de importancia en el campus universitario.\n" +
                "Cuenta con una gradería para 3000 espectadores.\n" +
                "Está adecuado para practicar varios deportes bajo su estructura; entre ellos patinaje, baloncesto, microfútbol y Balonvolea.\n" +
                "Tras varios intentos fallidos, finalmente en 1987 se ejecutó el proyecto de cubierta total del complejo deportivo.\n" +
                "Aquí también se encuentra el Museo de la Ciencia y el Juego.");
        map.put("761 Concha Acústica", b);
        b = new Building(R.drawable.i861, "Basada en una estructura en concreto, construida por el Ministerio de Obras Públicas para el Centro Administrativo Nacional (CAN) fue cedido a la Universidad Nacional para adaptar su estructura con el fin de albergar estudiantes casados.\n" +
                "Luego por algún tiempo permaneció fuera de uso hasta que finalmente se trasladó la sede administrativa y la rectoría de la Institución.");
        map.put("861 Edificio Uriel Gutiérrez", b);
        b = new Building(R.drawable.i862, "Estructura residencial concebida dentro del Programa de Bienestar Estudiantil.\n" +
                "Cuenta con 556 apartamentos los cuales fueron ocupados por estudiantes de intercambio y otros, su diseño se basó en la posibilidad de generar un ambiente familiar y seguro.\n" +
                "Durante su ocupación se realizaron una serie de cambios no aprobados, ejecutados por sus ocupantes, los cuales han alterado la hegemonía de la construcción.");
        map.put("862 Unidad Camilo Torres", b);
    }
}

class StringBuildings {
    public static final String[] strings = {
            "101 Torre de Enfermería",
            "102 Biblioteca Central Gabriel García Márquez",
            "103 Polideportivo",
            "104 Auditorio León de Greiff",
            "201 Facultad de Derecho, Ciencias Políticas y Sociales",
            "205 Edificio Orlando Fals Borda: Departamento de Sociología",
            "207 Museo de Arquitectura Leopoldo Rother",
            "210 Facultad de Odontología",
            "212 Aulas de Ciencias Humanas",
            "214 Edificio Antonio Nariño - Departamento de Lingüística. Departamento de Ingeniería Civil y Agrícola",
            "217 Edificio Francisco de Paula Santander: Diseño Gráfico",
            "224 Edificio Manuel Ancízar",
            "225 Edificio Rogelio Salmona de Postgrados en Ciencias Humanas",
            "229 Departamento de lenguas extranjeras",
            "231 Idiomas",
            "238 Contaduria / Postgrados de Ciencias Económicas",
            "239 Filosofía",
            "251 Capilla",
            "301 Escuela de Artes Plásticas",
            "303 Escuela de Arquitectura",
            "305 Conservatorio de Música",
            "309 Talleres y Aulas de Construcción",
            "310 Facultad de Ciencias Económicas",
            "311 Bloque II Facultad de Ciencias Económicas",
            "314 Postgrados en Arquitectura - SINDU",
            "317 Museo de Arte",
            "401 Facultad de Ingeniería / Edificio Viejo",
            "404 Departamentos de Matemáticas, Física y Estadística / Yu Takeuchi",
            "405 Postgrados en Matemáticas y Física",
            "406 IEI / Instituto de Extensión e Investigación",
            "407 Postgrados en Materiales y Procesos de Manufactura",
            "408 Laboratorio de Ensayos Hidráulicos",
            "409 Laboratorio de Hidráulica",
            "411 Laboratorios de Ingeniería",
            "412 Laboratorio de Ingeniería Química",
            "413 Observatorio Astronómico",
            "421 Departamento de Biología",
            "425 Instituto de Ciencias Naturales",
            "426 Instituto de Genética",
            "433 Almacén e Imprenta / Taller de Imprenta",
            "434 Instituto Pedagógico Arturo Ramírez Montúfar IPARM",
            "437 Centro de Acopio de Residuos Sólidos",
            "450 Departamento de Farmacia",
            "451 Departamento de Química",
            "452 Postgrados en Bio/Química y Carbones",
            "453 Aulas de Ingeniería",
            "454 Ciencia y Tecnología (CyT)",
            "471 Facultad de Medicina",
            "476 Facultad de Ciencias",
            "477 Aulas de Informática",
            "481 Facultad de Medicina Veterinaria y Zootecnia",
            "500 Facultad de Ciencias Agrarias",
            "571 Hemeroteca Nacional",
            "701 Departamento de Cine y Televisión",
            "731 Estadio de fútbol Alfonso López Pumarejo",
            "761 Concha Acústica",
            "861 Edificio Uriel Gutiérrez",
            "862 Unidad Camilo Torres",
    };
}



class Building {
    private int resId;
    private String info;

    public Building(int resId, String info) {
        this.resId = resId;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getImage() {
        return resId;
    }

    public void setImage(int resId) {
        this.resId = resId;
    }
}

