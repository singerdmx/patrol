require 'spec_helper'

describe "check_points/edit" do
  before(:each) do
    @check_point = assign(:check_point, stub_model(CheckPoint))
  end

  it "renders the edit check_point form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", check_point_path(@check_point), "post" do
    end
  end
end
