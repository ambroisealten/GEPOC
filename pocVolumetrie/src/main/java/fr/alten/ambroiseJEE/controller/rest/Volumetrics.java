/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.model.beans.Data;
import fr.alten.ambroiseJEE.model.repository.DataRepository;

/**
 * @author Andy Chabalier
 *
 */
@Controller
public class Volumetrics {
	class AddThread extends Thread {

		private final long start;
		private final long end;

		public AddThread(final long start, final long end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public void start() {
			for (long l = this.start; l < this.end; l++) {
				final Data data = new Data();
				data.setNumber(l);
				try {
					Volumetrics.this.dataRepository.save(data);
				} catch (final Exception e) {
				}
			}
			LoggerFactory.getLogger(Volumetrics.class).info(this.start + " - " + this.end + " finish");
		}
	}

	@Autowired
	private DataRepository dataRepository;

	private final Gson gson;

	public Volumetrics() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@PostMapping(path = "/volumetrics/populate")
	public @ResponseBody String addNewData(@RequestParam("nbModalite") final int alpha,
			@RequestParam("nbExamJour") final int beta, @RequestParam("nbSerieExam") final int gamma,
			@RequestParam("nbDataSerie") final int delta) {

		final long start = System.currentTimeMillis();

		final long nbData = alpha * 365 * beta * gamma * delta;

		final int nbDataPerProcessor = Runtime.getRuntime().availableProcessors();
		final List<AddThread> tList = new ArrayList<AddThread>();
		int current = 0;
		while (current < nbData) {
			tList.add(new AddThread(current, current + nbData / nbDataPerProcessor));
			current += nbData / nbDataPerProcessor;
		}
		tList.forEach(AddThread::start);

		LoggerFactory.getLogger(Volumetrics.class).info(String.valueOf(System.currentTimeMillis() - start));

		return nbData + " objects created";
	}

	@GetMapping(path = "/volumetrics/100")
	public @ResponseBody String getData() {
		return this.gson.toJson(this.dataRepository.findByNumberLessThanEqual(100));
	}

	@GetMapping(path = "/volumetrics")
	public @ResponseBody String getData(@RequestParam("nbModalite") final int alpha,
			@RequestParam("nbExamJour") final int beta, @RequestParam("nbSerieExam") final int gamma,
			@RequestParam("nbDataSerie") final int delta) {
		return this.gson.toJson(this.dataRepository.findByNumberLessThanEqual(alpha * 365 * beta * gamma * delta));
	}
}
