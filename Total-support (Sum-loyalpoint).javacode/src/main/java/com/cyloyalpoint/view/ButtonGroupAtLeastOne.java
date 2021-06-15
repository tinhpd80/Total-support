package com.cyloyalpoint.view;

import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 * A ButtonGroup for check-boxes enforcing that at least one remains selected.
 * 
 * When the group has exactly two buttons, deselecting the last selected one
 * automatically selects the other.
 * 
 * When the group has more buttons, deselection of the last selected one is
 * denied.
 */
public class ButtonGroupAtLeastOne extends ButtonGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Set<ButtonModel> selected = new HashSet<>();

	@Override
	public void setSelected(ButtonModel model, boolean b) {
		if (b && !this.selected.contains(model)) {
			select(model, true);
		} else if (!b && this.selected.contains(model)) {
			if (this.buttons.size() == 2 && this.selected.size() == 1) {
				select(model, false);
				AbstractButton other = this.buttons.get(0).getModel() == model ? this.buttons.get(1)
						: this.buttons.get(0);
				select(other.getModel(), true);
			} else if (this.selected.size() > 1) {
				this.selected.remove(model);
				model.setSelected(false);
			}
		}
	}

	private void select(ButtonModel model, boolean b) {
		if (b) {
			this.selected.add(model);
		} else {
			this.selected.remove(model);
		}
		model.setSelected(b);
	}

	@Override
	public boolean isSelected(ButtonModel m) {
		return this.selected.contains(m);
	}

	public void addAll(AbstractButton... buttons) {
		for (AbstractButton button : buttons) {
			add(button);
		}
	}

	public boolean isAtLeastOne() {
		for (AbstractButton button : buttons) {
			if (button.isSelected()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void add(AbstractButton button) {
		if (button.isSelected()) {
			this.selected.add(button.getModel());
		}
		if (!isAtLeastOne()) {
			button.setSelected(true);
			this.selected.add(button.getModel());
		}
		super.add(button);
	}
}